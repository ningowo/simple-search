package team.snof.simplesearch.search.engine;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.engine.ComplexEngineResult;
import team.snof.simplesearch.search.model.dao.index.Index;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhouyg
 * @date 2022/6/7
 */
@Component
public class EngineImpl implements Engine{
    @Override
    public ComplexEngineResult rangeFind(Map<String, Integer> wordToFreqMap, int offset, int limit) {
        List<Doc> docs = new ArrayList<>();
        docs.add(new Doc(1L,"img1", "字节跳动1"));
        docs.add(new Doc(2L,"img2", "字节跳动2"));
        docs.add(new Doc(3L,"img3", "字节跳动3"));

        List<Long> totalDocIds = new ArrayList<>();
        totalDocIds.add(1L);
        totalDocIds.add(2L);
        totalDocIds.add(3L);

        List<String> relatedSearch = new ArrayList<>();
        relatedSearch.add("字节");
        relatedSearch.add("跳动");
        ComplexEngineResult complexEngineResult = new ComplexEngineResult(docs, totalDocIds, relatedSearch);
        return complexEngineResult;
    }

    @Override
    public List<Doc> batchFindDocs(List<Long> docIds) {
        List<Doc> list = new ArrayList<>();
        list.add(new Doc(1L,"img1", "字节跳动1"));
        return list;
    }

    @Override
    public List<Index> batchFindIndexes(List<String> words) {
        List<Index> res = new ArrayList<>();
        List<Pair<Long, BigDecimal>> list = new ArrayList<>();
        Pair<Long, BigDecimal> pair = new ImmutablePair<Long, BigDecimal>(1L, new BigDecimal(1231231.232));
        list.add(pair);
        Index index = new Index(1L, list);
        res.add(new Index(1L, list));
        res.add(new Index(2L, list));
        res.add(new Index(3L, list));
        return res;
    }

    @Override
    public ComplexEngineResult find(Map<String, Integer> wordToFreqMap) {
        return null;
    }

    @Override
    public Doc findDoc(Long docId) {
        return null;
    }

    @Override
    public Index findIndex(String word) {
        return null;
    }


}
