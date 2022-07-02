package team.snof.simplesearch.search.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.ForwardIndex;
import team.snof.simplesearch.search.model.dao.Doc;
import team.snof.simplesearch.search.model.dao.InvertedIndex;
import team.snof.simplesearch.search.storage.DocStorage;
import team.snof.simplesearch.search.storage.ForwardIndexStorage;
import team.snof.simplesearch.search.storage.InvertedIndexStorage;

import java.util.*;

@Slf4j
@Component
public class EngineImpl implements Engine {

    @Autowired
    ForwardIndexStorage forwardIndexStorage;

    @Autowired
    InvertedIndexStorage invertedIndexStorage;

    @Autowired
    DocStorage docStorage;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    SortLogic sortLogic;

    //索引redis格式串
    private final String indexRedisFormat = "engine:index:%s:string";

    //倒排索引缓存时间(min)
    private final int expireDuration = 10;

    // 最多需要获取的相关搜索条数
    private static final int MAX_NUM_RELATED_SEARCH_TO_FIND = 8;

    /**
     * 根据query查找索引并排序文档
     */
    public List<String> findSortedDocIds(Map<String, Integer> wordToFreqMap) {
        // 1. 获取所有文档统计数据
        // todo 这里long可能不太准确
        long docAvgLen = forwardIndexStorage.getDocAvgLen();
        log.info("[findSortedDocIds] 文档平均长度获取完成：{}", docAvgLen);

        long totalDocNum = forwardIndexStorage.getTotalDocNum();
        log.info("[findSortedDocIds] 文档总数获取完成：{}", totalDocNum);

        // 计算单个分词关联度
        List<String> wordList = new ArrayList<>(wordToFreqMap.keySet());
        List<Map<String, Double>> wordToDocCorrMap = new ArrayList<>(wordList.size());
        for (String word : wordList) {
            // 2. 获取倒排索引，文档召回（全部出现分词的文档）
            InvertedIndex invertedIndex = invertedIndexStorage.find(word);
            if (invertedIndex == null) {
                continue;
            }
            List<String> docIdList = invertedIndex.getDocIds();
            // todo 这里花时间第二多
            log.info("[findSortedDocIds] docid召回完成。分词：{} docId数量：{}", invertedIndex.getWord(), invertedIndex.getDocIds().size());

            // 3. 获取正排索引（docId对应的文档具体信息，用于计算关联度）
            List<ForwardIndex> forwardIndices = forwardIndexStorage.batchFind(docIdList);
            if (forwardIndices.isEmpty()) {
                continue;
            }
            // todo 这里花时间很多
            log.info("[findSortedDocIds] forwardIndices获取完成：{}", forwardIndices.size());

            Map<String, Double> docToCorrMap = sortLogic.calcSingleWordCorr(word, forwardIndices, totalDocNum, docAvgLen, wordToFreqMap);
            wordToDocCorrMap.add(docToCorrMap);
        }

        log.info("[findSortedDocIds] 关联度计算完成，分词数量：文档数量：{}", wordToDocCorrMap);

        // 排序
        return sortLogic.sortAllDocs(wordToDocCorrMap);
    }

    /**
     * 获取相关搜索结果
     */
    public List<String> findRelatedSearch(List<String> relatedSearchDocIds, Map<String, Integer> wordToFreqMap) {
        List<String> relatedSearchList = new ArrayList<>();
        // 若只包含一个分词
        if (wordToFreqMap.size() == 1) {
            String keyWord = wordToFreqMap.keySet().iterator().next();
            for (String docId : relatedSearchDocIds) {
                String capation = docStorage.getDocById(docId).getCaption();
                relatedSearchList.add(sortLogic.calRelatedSearch(capation, keyWord));
                if (relatedSearchList.size() == MAX_NUM_RELATED_SEARCH_TO_FIND) break;
            }
        } else if (wordToFreqMap.size() == 2){  // 只包含两个分词

        }
    }

    /**
     * 获取正排索引
     */
    public List<ForwardIndex> findForwardIndex(String word){
        // 召回所有包含分词的docId
        InvertedIndex index = invertedIndexStorage.find(word);
        if (index == null) {
            return Collections.emptyList();
        }
        List<String> docIdList = index.getDocIds();

        // 获取正排索引
        return forwardIndexStorage.batchFind(docIdList);
    }

    @Override
    public List<InvertedIndex> findInvertedIndexList(List<String> wordList) {
        return invertedIndexStorage.batchFind(wordList);
    }

    /**
     * 查询单个文档
     */
    public Doc findDoc(String docId){
        return docStorage.getDocById(docId);
    }

    /**
     * 批量查询文档
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

}
