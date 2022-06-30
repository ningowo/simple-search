package team.snof.simplesearch.search.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.InvertedIndex;

import java.util.List;

@Slf4j
@Component
public class InvertedIndexStorage {

    @Autowired
    MongoTemplate mongoTemplate;

    public void save(InvertedIndex index) {
        mongoTemplate.save(index, "inverted_index");
    }

    public void save(String word, String docId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(word));
        Update update = new Update().push("docIds", docId);
        mongoTemplate.upsert(query, update, InvertedIndex.class, "inverted_index");
    }

    public InvertedIndex find(String word) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(word));
        return mongoTemplate.findOne(query, InvertedIndex.class, "inverted_index");
    }

    public List<InvertedIndex> batchFind(List<String> wordList) {
        Query query = new Query().addCriteria(Criteria.where("_id").in(wordList));
        return mongoTemplate.find(query, InvertedIndex.class, "inverted_index");
    }

}
