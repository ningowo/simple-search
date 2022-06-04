package team.snof.simplesearch.search.engine;

import team.snof.simplesearch.search.model.bo.CompleteResult;
import team.snof.simplesearch.search.model.bo.CompleteResultWithRange;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.vo.DocVO;
import team.snof.simplesearch.search.model.vo.SearchResponseVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Engine {

    // 整体结果查询
    CompleteResult find(Map<String, Integer> wordToFreqMap);

    CompleteResultWithRange rangeFind(Map<String, Integer> wordToFreqMap, int offset, int limit);

    // 文档查询
    Doc findDoc(Long docId);

    List<Doc> batchFindDocs(List<Long> docIds); // 常用

    // 索引查询
    Index findIndex(String word);

    List<Index> batchFindIndexes(List<String> words);

}
