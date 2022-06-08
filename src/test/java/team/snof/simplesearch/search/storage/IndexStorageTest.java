package team.snof.simplesearch.search.storage;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import team.snof.simplesearch.search.model.dao.index.Index;

import java.util.List;

@SpringBootTest
@Slf4j
public class IndexStorageTest {
    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void findTest() {
        String key = "西瓜";
        Query query = new Query(Criteria.where("indexKey").is(key));
        List<Index> indices = mongoTemplate.find(query, Index.class);
        System.out.println(indices);
    }
}
