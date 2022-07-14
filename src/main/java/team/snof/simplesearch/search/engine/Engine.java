package team.snof.simplesearch.search.engine;

import team.snof.simplesearch.search.model.dao.ForwardIndex;
import team.snof.simplesearch.search.model.dao.InvertedIndex;
import team.snof.simplesearch.search.model.dao.Doc;

import java.util.List;
import java.util.Map;

public interface Engine {

    /**
     * 根据query获取完整的排好序的docIds
     * 倒排 -> 正排 -> 排序
      */
    List<String> findSortedDocIds(Map<String, Integer> wordToFreqMap);

    // 相关搜索  传入DocId进行计算
    List<String> findRelatedSearchById(List<String> relatedSearchDocIds, Map<String, Integer> wordToFreqMap);

    // 相关搜索  传入Doc进行计算
    List<String> findRelatedSearchByDoc(List<Doc> relatedSearchDocs, Map<String, Integer> wordToFreqMap);

    // 倒排索引查询
    List<InvertedIndex> findInvertedIndexList(List<String> wordList);

    // 正排索引查询
    List<ForwardIndex> findForwardIndex(String word);

    // 文档查询
    Doc findDoc(String docId);

    List<Doc> batchFindDocs(List<String> docIds);

}
