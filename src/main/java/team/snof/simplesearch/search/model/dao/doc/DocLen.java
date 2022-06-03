package team.snof.simplesearch.search.model.dao.doc;

public class DocLen {
    private long docId;
    private long docLen;

    public DocLen(long docId, long docLen) {
        this.docId = docId;
        this.docLen = docLen;
    }

    public long getDocId() {
        return docId;
    }

    public void setDocId(long docId) {
        this.docId = docId;
    }

    public long getDocLen() {
        return docLen;
    }

    public void setDocLen(long docLen) {
        this.docLen = docLen;
    }
}
