package team.snof.simplesearch.search.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import team.snof.simplesearch.search.mapper.DocLenMapper;
import team.snof.simplesearch.search.model.dao.DocLen;

@Slf4j
public class DocLenService {
    @Autowired
    private DocLenMapper docLenMapper;

    public void saveDocLen(long doc_id, long doc_len) {
        try {
            // 调用云存储或者本地存储
            // 相当于service层  实现装配  调用mapper存到数据库
            DocLen docLen = new DocLen();
            docLen.setDocLen(doc_len);
            docLen.setDocId(doc_id);

            docLenMapper.insertDocLen(docLen);
        } catch (Exception e) {
            log.error("");
        }
    }

    public long getDocAveLen() {
        return docLenMapper.getDocAveLen();
    }

    public long getDocNum() {
        return docLenMapper.getDocNum();
    }

    public long getDocLen(long doc_id) {
        return docLenMapper.getDocLen(doc_id);
    }

}
