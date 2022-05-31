package team.snof.simplesearch.search.model.dao;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Index {
    public String indexKey; // 分词

    public List<DocInfo> docInfoList;

    public Index(String indexKey, List<DocInfo>docInfoList) {
        this.indexKey = indexKey;
        this.docInfoList = docInfoList;
    }
}
