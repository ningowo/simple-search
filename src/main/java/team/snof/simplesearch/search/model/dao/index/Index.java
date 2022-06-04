package team.snof.simplesearch.search.model.dao.index;

import lombok.Data;
import team.snof.simplesearch.search.model.dao.doc.DocInfo;

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
