package team.snof.simplesearch.search.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.Doc;

import java.util.List;

@Component
public class DocStorage {

    @Autowired
    MongoTemplate mongoTemplate;

    public Doc getDocById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, Doc.class, "doc_storage");
    }

    public String addDoc(Doc doc) {
        if (doc == null) {
            return null;
        }

        Doc doc_storage = mongoTemplate.save(doc, "doc_storage");
        return doc_storage.getId();
    }
}