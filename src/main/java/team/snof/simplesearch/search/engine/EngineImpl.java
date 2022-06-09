package team.snof.simplesearch.search.engine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.storage.OssStorage;
import team.snof.simplesearch.search.model.dao.engine.ComplexEngineResult;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.index.Index;
import org.springframework.data.redis.core.RedisTemplate;
import team.snof.simplesearch.search.storage.IndexStorage;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class EngineImpl implements Engine {

    @Autowired
    IndexStorage indexStorage;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    SortLogic sortLogic;

    private final String indexRedisFormat = "engine:index:%s:string";//索引redis格式串
    private final int expireDuration = 10;//倒排索引缓存时间(min)
    //返回全部文档结果
    public ComplexEngineResult find(Map<String, Integer> wordToFreqMap){
        // 获取索引
        List<String> words = new ArrayList<>(wordToFreqMap.keySet());
        List<Index> indexs = batchFindIndexes(words);

        // 文档排序
        List<Long> docIds = sortLogic.docSort(indexs,wordToFreqMap);

        // 获取文档
        List<Doc> docs = batchFindDocs(docIds);

        // 计算相关搜索结果
        List<String> relatedSearches = sortLogic.wordSort(docs, wordToFreqMap);

        return new ComplexEngineResult(docs, docIds, relatedSearches);
    }

    public List<Long> findSortedDocIds(Map<String, Integer> wordToFreqMap) {
        // 获取索引
        List<String> words = new ArrayList<>(wordToFreqMap.keySet());
        List<Index> indexs = batchFindIndexes(words);

        // 文档排序
        return sortLogic.docSort(indexs,wordToFreqMap);
    }

    public List<String> findRelatedSearch(List<Doc> docs, Map<String, Integer> wordToFreqMap) {
        return sortLogic.wordSort(docs, wordToFreqMap);
    }

    //返回指定文档结果
    public ComplexEngineResult rangeFind(Map<String, Integer> wordToFreqMap, int offset, int limit){
        // 获取分词
        List<String> words = new ArrayList<>(wordToFreqMap.keySet());

        List<Index> indexs = batchFindIndexes(words);
        List<Long> docIds = sortLogic.docSort(indexs,wordToFreqMap);
        List<Long> partialDocIds = new ArrayList<>();
        for(int i = offset; i < docIds.size() && i < offset + limit; ++i){//避免越界
            partialDocIds.add(docIds.get(i));
        }
        List<Doc> docs = batchFindDocs(partialDocIds);
        List<String> relatedSearches = sortLogic.wordSort(docs, wordToFreqMap);
        return new ComplexEngineResult(docs,docIds, relatedSearches);
    }

    // 文档查询
    public Doc findDoc(Long docId){
        return OssStorage.getBySnowId(docId);
    }

    //批查询文档
    public List<Doc> batchFindDocs(List<Long> docIds){// 常用
        List<Doc> docs = new ArrayList<>();
        for(Long docId: docIds){
            docs.add(OssStorage.getBySnowId(docId));
        }
        return docs;
    }

    // 索引查询
    public Index findIndex(String word){
        //redis查询
        String wordRedisKey = String.format(indexRedisFormat,word);
        Index index = (Index)redisTemplate.opsForValue().get(wordRedisKey);

        //判断是否有缓存
        if (index == null) {
            index = indexStorage.findByKey(word).get(0);
            redisTemplate.opsForValue().set(wordRedisKey, index);
            redisTemplate.expire(wordRedisKey, expireDuration, TimeUnit.MINUTES);
        }
        return index;
    }

    /**
     * 只返回非空索引
     * @param words
     * @return
     */
    public List<Index> batchFindIndexes(List<String> words){
        if (words.isEmpty()) {
            return Collections.emptyList();
        }

        //redis查询
        // 构建Redis查询keys
        List<String> wordRedisKeys = new ArrayList<>(words.size());
        for(int i = 0; i < words.size(); ++i){
            wordRedisKeys.add(i, String.format(indexRedisFormat,words.get(i)));
        }
        List<Index> indexs = redisTemplate.opsForValue().multiGet(wordRedisKeys);

        //判断是否有缓存
        for(int i = 0; i < indexs.size(); ++i) {
            if(indexs.get(i) == null){
                // 查数据库
                List<Index> index = indexStorage.findByKey(words.get(i));
                if (index.isEmpty()) {
                    continue;
                }

                indexs.set(i, index.get(0));
                redisTemplate.opsForValue().set(wordRedisKeys.get(i), indexs.get(i));
                redisTemplate.expire(wordRedisKeys.get(i), expireDuration, TimeUnit.MINUTES);
            }
        }
        indexs.removeIf(Objects::isNull);
        return indexs;
    }
}
