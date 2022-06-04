package team.snof.simplesearch.search.engine.storage;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.doc.Doc;

import java.util.List;

@Slf4j
@Component
public class DocStorage {
    @Autowired
    MongoTemplate mongoTemplate;

    public List<Doc> findAll() {
        return mongoTemplate.findAll(Doc.class);
    }

    public List<Doc> findById(Long id) {
        Query query = new Query(Criteria.where("SnowflakeDocId").is(id));
        return mongoTemplate.find(query, Doc.class);
    }

    public void saveBatch(List<Doc> docs) {
        mongoTemplate.insert(docs, Doc.class);
    }

    public void save(Doc doc) {
        mongoTemplate.save(doc);
    }


    public Long deleteById(Long id) {
        Query query = new Query(Criteria.where("SnowflakeDocId").is(id));
        DeleteResult remove = mongoTemplate.remove(query, Doc.class);
        return remove.getDeletedCount();
    }

    public Long updateById(Long id, Doc doc) {
        Query query = new Query(Criteria.where("SnowflakeDocId").is(id));
        Update update = new Update();
        update.set("url", doc.getUrl());
        update.set("caption", doc.getCaption());
        UpdateResult result = mongoTemplate.updateFirst(query, update, Doc.class);
        return result.getModifiedCount();
    }
}
