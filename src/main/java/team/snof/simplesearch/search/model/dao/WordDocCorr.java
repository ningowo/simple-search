package team.snof.simplesearch.search.model.dao;

import java.math.BigDecimal;

public class WordDocCorr {

    private String word;
    private long doc_id;
    private BigDecimal corr;

    public WordDocCorr(String word, long doc_id, BigDecimal corr) {
        this.word = word;
        this.doc_id = doc_id;
        this.corr = corr;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public long getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(long doc_id) {
        this.doc_id = doc_id;
    }

    public BigDecimal getCorr() {
        return corr;
    }

    public void setCorr(BigDecimal corr) {
        this.corr = corr;
    }
}
