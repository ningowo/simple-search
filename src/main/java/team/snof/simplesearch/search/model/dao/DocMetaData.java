package team.snof.simplesearch.search.model.dao;


public class DocMetaData {
    private Long docNum;    // 文档总数目
    private Long docLen;    // 文档总长度
    private Long avgLen;    // 文档的平均长度

    public Long getDocNum() {
        return docNum;
    }

    public void setDocNum(Long docNum) {
        this.docNum = docNum;
    }

    public Long getDocLen() {
        return docLen;
    }

    public void setDocLen(Long docLen) {
        this.docLen = docLen;
    }

    public Long getAvgLen() {
        return avgLen;
    }

    public void setAvgLen(Long avgLen) {
        this.avgLen = avgLen;
    }
}
