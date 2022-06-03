package team.snof.simplesearch.search.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import team.snof.simplesearch.search.model.dao.doc.DocLen;

public class DocLenStorage {
    @Autowired
    MongoTemplate mongoTemplate;

    public void save(DocLen docLen) {
        mongoTemplate.save(docLen);
    }

    public long getDocTotalNum() {
        return mongoTemplate.getCollection("doc_length").estimatedDocumentCount();
    }

    public long getDocAveLen() {
        // TODO  这里求平均值的语句有问题
        return mongoTemplate.aggregate({$group:{_id:null, avg:{$avg:"$docLen"}}});
    }

    public long getDocLen(long docId) {
        Query query = new Query().addCriteria(Criteria.where("docId").is(docId));
        return mongoTemplate.findOne(query, DocLen.class).getDocLen();
    }

}
