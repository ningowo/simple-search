package team.snof.simplesearch.search.model.dao;

import java.util.HashMap;
import java.util.List;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.index.Index;

public interface Engine {

    // 整体结果查询
    CompleteResult find(HashMap<String, Integer> wordToFreqMap);

    RangeResult rangeFind(HashMap<String, Integer> wordToFreqMap, int offset, int limit);

    // 文档查询
    Doc findDoc(Long docId);

    List<Doc> batchFindDocs(List<Long> docIds); // 常用

    // 索引查询
    Index findIndex(String word);

    List<Index> batchFindIndexs(List<String> words);

}