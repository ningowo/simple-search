package team.snof.simplesearch.search.engine;

import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.engine.ComplexEngineResult;
import team.snof.simplesearch.search.model.dao.index.Index;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Engine {

    // 整体结果查询
    ComplexEngineResult find(Map<String, Integer> wordToFreqMap) throws IOException;

    ComplexEngineResult rangeFind(Map<String, Integer> wordToFreqMap, int offset, int limit) throws IOException;

    // 文档查询
    Doc findDoc(Long docId);

    List<Doc> batchFindDocs(List<Long> docIds); // 常用

    // 索引查询
    Index findIndex(String word);

    List<Index> batchFindIndexes(List<String> words);

}
