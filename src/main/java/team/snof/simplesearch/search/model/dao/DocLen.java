package team.snof.simplesearch.search.model.dao;

public class DocLen {
    // 实体里面的属性定义 需要使用驼峰式  只有mysql内部使用下划线就行
    private long docId;
    private long docLen;

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
