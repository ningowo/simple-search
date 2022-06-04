package team.snof.simplesearch.search.model.dao.doc;

import java.math.BigDecimal;

public class DocInfo {
    private Long docId; // 文档的id
    private BigDecimal corr;  // 相关度系数

    public DocInfo(long doc_id, BigDecimal corr) {
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public BigDecimal getCorr() {
        return corr;
    }

    public void setCorr(BigDecimal corr) {
        this.corr = corr;
    }
}
