package team.snof.simplesearch.search.engine;

import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.bo.CompleteResult;
import team.snof.simplesearch.search.model.bo.CompleteResultWithRange;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.index.Index;

import java.util.List;
import java.util.Map;

@Component
public class EngineImpl implements Engine {

    @Override
    public CompleteResult find(Map<String, Integer> wordToFreqMap) {
        return null;
    }

    @Override
    public CompleteResultWithRange rangeFind(Map<String, Integer> wordToFreqMap, int offset, int limit) {


        return null;
    }

    @Override
    public Doc findDoc(Long docId) {
        return null;
    }

    @Override
    public List<Doc> batchFindDocs(List<Long> docIds) {


        return null;
    }

    @Override
    public Index findIndex(String word) {
        return null;
    }

    @Override
    public List<Index> batchFindIndexes(List<String> words) {


        return null;
    }
}
