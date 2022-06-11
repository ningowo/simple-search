package team.snof.simplesearch.search.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.engine.ComplexEngineResult;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.storage.IndexStorage;
import team.snof.simplesearch.search.storage.DocStorage;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class EngineImpl implements Engine {

    @Autowired
    IndexStorage indexStorage;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    SortLogic sortLogic;

    @Autowired
    DocStorage docStorage;

    //索引redis格式串
    private final String indexRedisFormat = "engine:index:%s:string";

    //倒排索引缓存时间(min)
    private final int expireDuration = 10;

    /**
     * 获取索引并据此排序文档
     * @param wordToFreqMap
     * @return
     */
    public List<String> findSortedDocIds(Map<String, Integer> wordToFreqMap) {
        // 获取索引
        List<String> words = new ArrayList<>(wordToFreqMap.keySet());
        List<Index> indexs = batchFindIndexes(words);

        // 文档排序
        return sortLogic.docSort(indexs,wordToFreqMap);
    }

    /**
     * 获取相关搜索结果
     * @param docs
     * @param wordToFreqMap
     * @return
     */
    public List<String> findRelatedSearch(List<Doc> docs, Map<String, Integer> wordToFreqMap) {
        return sortLogic.wordSort(docs, wordToFreqMap);
    }

    /**
     * 查询单个文档
     * @param docId
     * @return
     */
    public Doc findDoc(String docId){
        return docStorage.getDocById(docId);
    }

    /**
     * 批量查询文档
     * @param docIds
     * @return
     */
    public List<Doc> batchFindDocs(List<String> docIds){// 常用
        log.info("开始批量获取文档，请求文档数量为: " + docIds.size());
        List<Doc> docs = new ArrayList<>();
        for(String docId: docIds){
            docs.add(docStorage.getDocById(docId));
        }
        log.info("文档获取完成! 文档数量为:" + docs.size());
        return docs;
    }

    /**
     * 获取单个索引
     * @param word
     * @return
     */
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
     * 批量获取索引，只返回非空索引
     * @param words
     * @return
     */
    public List<Index> batchFindIndexes(List<String> words) {
        if (words.isEmpty()) {
            return Collections.emptyList();
        }
        log.info("开始批量获取索引: " + words);

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

        log.info("批量获取索引完成! 分词: " + words + ", 索引数量: " + indexs.size());

        return indexs;
    }

    /**
     * 返回指定范围文档结果
     */
    public ComplexEngineResult rangeFind(Map<String, Integer> wordToFreqMap, int offset, int limit) {
        // 获取分词
        List<String> words = new ArrayList<>(wordToFreqMap.keySet());

        List<Index> indexs = batchFindIndexes(words);
        List<String> docIds = sortLogic.docSort(indexs,wordToFreqMap);
        List<String> partialDocIds = new ArrayList<>();
        for(int i = offset; i < docIds.size() && i < offset + limit; ++i){//避免越界
            partialDocIds.add(docIds.get(i));
        }
        List<Doc> docs = batchFindDocs(partialDocIds);
        List<String> relatedSearches = sortLogic.wordSort(docs, wordToFreqMap);
        return new ComplexEngineResult(docs,docIds, relatedSearches);
    }

    /**
     * 返回全部文档完整结果
     * @param wordToFreqMap
     * @return
     */
    public ComplexEngineResult find(Map<String, Integer> wordToFreqMap){
        // 获取索引
        List<String> words = new ArrayList<>(wordToFreqMap.keySet());
        List<Index> indexs = batchFindIndexes(words);

        // 文档排序
        List<String> docIds = sortLogic.docSort(indexs,wordToFreqMap);

        // 获取文档
        List<Doc> docs = batchFindDocs(docIds);

        // 计算相关搜索结果
        List<String> relatedSearches = sortLogic.wordSort(docs, wordToFreqMap);

        return new ComplexEngineResult(docs, docIds, relatedSearches);
    }
}
