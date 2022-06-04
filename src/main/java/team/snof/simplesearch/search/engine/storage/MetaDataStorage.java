package team.snof.simplesearch.search.engine.storage;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.meta.DocMetaData;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class MetaDataStorage {
    @Autowired
    MongoTemplate mongoTemplate;
    ReentrantLock lock = new ReentrantLock();

    public DocMetaData find() {
        List<DocMetaData> all = mongoTemplate.findAll(DocMetaData.class);
        return all.get(0);
    }

    public void save(DocMetaData docMetaData) {
        mongoTemplate.save(docMetaData);
    }

    public Long delete(DocMetaData docMetaData) {
        DeleteResult remove = mongoTemplate.remove(docMetaData);
        return remove.getDeletedCount();
    }

    public Long update(DocMetaData docMetaData) {
        Query query = new Query();
        Update update = new Update();
        update.set("docNum", docMetaData.getDocNum());
        update.set("docLen", docMetaData.getDocLen());
        update.set("avgLen", docMetaData.getDocLen() / docMetaData.getDocNum());
        UpdateResult result = mongoTemplate.updateFirst(query, update, DocMetaData.class);
        return result.getModifiedCount();
    }

    /**
     * 添加文档时，修改文档的总数量和总长度
     */
    public void addDoc(Doc doc) {
        lock.lock();
        DocMetaData docMetaData = find();
        docMetaData.setDocNum(docMetaData.getDocNum() + 1);
        docMetaData.setDocLen(docMetaData.getDocLen() + doc.getCaption().length());
        docMetaData.setAvgLen(docMetaData.getDocLen() / docMetaData.getDocNum());
        update(docMetaData);
        lock.unlock();
    }
}
