package team.snof.simplesearch.search.model.dao;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.List;

@Data
public class  Index {

    // 转码过的transcoded中文或英文分词  这里最后存储在MangoDB的时候再转码?
    private String IndexKey;

    // id, 关联度
    private List<Pair<Long, BigDecimal>> docIdAndCorrList;

    public Index(String indexKey, List<Pair<Long, BigDecimal>> docIdAndCorrList) {
        IndexKey = indexKey;
        this.docIdAndCorrList = docIdAndCorrList;
    }

    public String getIndexKey() {
        return IndexKey;
    }

    public void setIndexKey(String indexKey) {
        IndexKey = indexKey;
    }

    public List<Pair<Long, BigDecimal>> getDocIdAndCorrList() {
        return docIdAndCorrList;
    }

    public void setDocIdAndCorrList(List<Pair<Long, BigDecimal>> docIdAndCorrList) {
        this.docIdAndCorrList = docIdAndCorrList;
    }
}
