package team.snof.simplesearch.search.model.dao;

public class TempData {
    private long doc_id;
    private long wordFreq;
    private long doc_len;

    public TempData(long docId, long wordFreq, long docLen) {
    }

    public long getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(long doc_id) {
        this.doc_id = doc_id;
    }

    public long getWordFreq() {
        return wordFreq;
    }

    public void setWordFreq(long wordFreq) {
        this.wordFreq = wordFreq;
    }

    public long getDoc_len() {
        return doc_len;
    }

    public void setDoc_len(long doc_len) {
        this.doc_len = doc_len;
    }

}
