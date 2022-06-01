package team.snof.simplesearch.search.model.dao.doc;

import java.math.BigDecimal;

public class DocInfo {
    private Long docId; // 文档的id
    private Long freq;  // 词频
    private BigDecimal corr;  // 相关度系数

    public DocInfo(long doc_id, long wordFreq, BigDecimal corr) {
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public Long getFreq() {
        return freq;
    }

    public void setFreq(Long freq) {
        this.freq = freq;
    }

    public BigDecimal getCorr() {
        return corr;
    }

    public void setCorr(BigDecimal corr) {
        this.corr = corr;
    }
}
