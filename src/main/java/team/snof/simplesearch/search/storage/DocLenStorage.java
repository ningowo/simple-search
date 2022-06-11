package team.snof.simplesearch.search.storage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.result.InsertManyResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.doc.DocLen;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DocLenStorage {
    @Autowired
    MongoTemplate mongoTemplate;

    public void save(DocLen docLen) {
        mongoTemplate.save(docLen, "doc_length");
    }

    public long getDocTotalNum() {
        return mongoTemplate.getCollection("doc_storage").countDocuments();
    }

    public long getDocAveLen() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("_id").avg("docLen").as("avgLen")
        );
        List<Map> results = mongoTemplate.aggregate(aggregation, "doc_length", Map.class).getMappedResults();
        if (results.isEmpty()) {
            return 0L;
        }
        return Math.round((Double) results.get(0).get("avgLen"));
    }

    public long getDocLen(String docId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(docId));
        DocLen doc_length = mongoTemplate.findOne(query, DocLen.class, "doc_length");

        if (doc_length == null) {
            return 0L;
        }
        return doc_length.getDocLen();

    }

}
