package team.snof.simplesearch.search.model.dao;

public class TempData {
    private long docId;
    private long wordFreq;

    public TempData(long docId, long wordFreq) {
    }

    public long getDocId() {
        return docId;
    }

    public void setDocId(long docId) {
        this.docId = docId;
    }

    public long getWordFreq() {
        return wordFreq;
    }

    public void setWordFreq(long wordFreq) {
        this.wordFreq = wordFreq;
    }
}
