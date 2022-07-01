package team.snof.simplesearch.search.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.ForwardIndex;

import java.util.List;
import java.util.Map;

@Component
public class ForwardIndexStorage {

    @Autowired
    MongoTemplate mongoTemplate;

    private long docAvgLen;

    private long totalDocNum;

    @Autowired
    private void setDocAvgLen() {
        AggregationOptions options = AggregationOptions.builder().allowDiskUse(true).build();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group().avg("docLength").as("avgLen")
        ).withOptions(options);
        List<Map> result = mongoTemplate.aggregate(aggregation, "forward_index", Map.class).getMappedResults();

        docAvgLen = result.isEmpty() ? 1L : Math.round((Double) result.get(0).get("avgLen"));
    }

    @Autowired
    private void setTotalDocNum() {
        Query query = new Query();
        totalDocNum = mongoTemplate.count(query, "forward_index");
    }

    public void save(ForwardIndex index) {
        mongoTemplate.save(index, "forward_index");
    }

    public ForwardIndex find(String docId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(docId));

        return mongoTemplate.findOne(query, ForwardIndex.class, "forward_index");
    }

    public List<ForwardIndex> batchFind(List<String> docIdList) {
        Query query = new Query().addCriteria(Criteria.where("_id").in(docIdList));

        return mongoTemplate.find(query, ForwardIndex.class, "forward_index");
    }

    public long getDocAvgLen() {
        return docAvgLen;
    }

    public long getTotalDocNum() {
        return totalDocNum;
    }

}
