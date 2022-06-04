package team.snof.simplesearch.search.engine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.OssUtil;
import team.snof.simplesearch.search.engine.storage.IndexStorage;
import team.snof.simplesearch.search.model.dao.CompleteResult;
import team.snof.simplesearch.search.model.dao.Engine;
import team.snof.simplesearch.search.model.dao.RangeResult;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.index.Index;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class EngineImpl implements Engine {
    @Autowired
    IndexStorage indexStorage;
    @Autowired
    EngineImpl engineImpl;
    @Autowired
    SortLogic sortLogic;
    @Autowired
    RedisTemplate redisTemplate;

    private final String indexRedisFormat = "engine:index:%s:string";//索引redis格式串
    private final int expireDuration = 10;//倒排索引缓存时间(min)
    //返回全部文档结果
    public CompleteResult find(HashMap<String, Integer> wordToFreqMap){
         // 获取分词
         List<String> words = new ArrayList<>();
         for(String word : wordToFreqMap.keySet()) words.add(word);

         List<Index> indexs = engineImpl.batchFindIndexs(words);
         List<Long> docIds = sortLogic.DocSort(indexs,wordToFreqMap);
         List<Doc> docs = engineImpl.batchFindDocs(docIds);
         return new CompleteResult(docs,docIds,sortLogic.wordSort(docs));
    }

    //返回指定文档结果
    public RangeResult rangeFind(HashMap<String, Integer> wordToFreqMap, int offset, int limit){
        // 获取分词
        List<String> words = new ArrayList<>();
        for(String word : wordToFreqMap.keySet()) words.add(word);

        List<Index> indexs = engineImpl.batchFindIndexs(words);
        List<Long> docIds = sortLogic.DocSort(indexs,wordToFreqMap);
        List<Long> partialDocIds = new ArrayList<>();
        for(int i = offset, upper = offset + limit; i < docIds.size() && i < upper; ++i){//避免越界
            partialDocIds.add(docIds.get(i));
        }
        List<Doc> docs = engineImpl.batchFindDocs(partialDocIds);
        return new RangeResult(docs,docIds,sortLogic.wordSort(docs));
    }

    // 文档查询
    public Doc findDoc(Long docId){
        return OssUtil.getBySnowId(docId);
    }

    //批查询文档
    public List<Doc> batchFindDocs(List<Long> docIds){// 常用
        List<Doc> docs = new ArrayList<>();
        for(Long docId: docIds){
            docs.add(OssUtil.getBySnowId(docId));
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

    //批查询索引
    public List<Index> batchFindIndexs(List<String> words){
        //redis查询
        List<String> wordRedisKeys = new ArrayList<>(words.size());
        for(int i = 0; i < words.size(); ++i){
            wordRedisKeys.set(i, String.format(indexRedisFormat,words.get(i)));
        }
        List<Index> indexs = redisTemplate.opsForValue().multiGet(wordRedisKeys);

        //判断是否有缓存
        for(int i = 0; i < indexs.size(); ++i){
            if(indexs.get(i) == null){
                indexs.set(i, indexStorage.findByKey(words.get(i)).get(0));
                redisTemplate.opsForValue().set(wordRedisKeys.get(i), indexs.get(i));
                redisTemplate.expire(wordRedisKeys.get(i), expireDuration, TimeUnit.MINUTES);
            }
        }
        return indexs;
    }
}
