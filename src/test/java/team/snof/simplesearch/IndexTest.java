package team.snof.simplesearch;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import team.snof.simplesearch.search.engine.storage.DocStorage;
import team.snof.simplesearch.search.engine.storage.IndexStorage;
import team.snof.simplesearch.search.model.dao.index.DocInfo;
import team.snof.simplesearch.search.model.dao.index.Index;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class IndexTest {
    @Autowired
    DocStorage docStorage;
    @Autowired
    IndexStorage indexStorage;
    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    void indexStorageTest() {
        Index index = new Index();
        index.setIndexKey("西瓜");
        DocInfo d1 = new DocInfo(1L, 1L, new BigDecimal("3.5"));
        DocInfo d2 = new DocInfo(2L, 2L, new BigDecimal("3.5"));
        DocInfo d3 = new DocInfo(3L, 3L, new BigDecimal("3.5"));
        index.getDocInfoList().add(d1);
        index.getDocInfoList().add(d2);
        index.getDocInfoList().add(d3);
        indexStorage.save(index);
    }

    @Test
    void insertBatch() {
        List<Index> indices = new ArrayList<>();
        indices.add(new Index("1", new ArrayList<>()));
        indices.add(new Index("2", new ArrayList<>()));
        indexStorage.saveBatch(indices);
    }

    @Test
    void deleteIndexTest() {
        String key = "2";
        Long num = indexStorage.deleteByKey(key);
        System.out.println(num);
    }

    @Test
    void updateIndexTest() {
        Index index = new Index();
        index.setIndexKey("苹果");
        DocInfo d1 = new DocInfo(1L, 1L, new BigDecimal("3.6"));
        DocInfo d2 = new DocInfo(2L, 2L, new BigDecimal("3.6"));
        DocInfo d3 = new DocInfo(3L, 3L, new BigDecimal("3.6"));
        index.getDocInfoList().add(d1);
        index.getDocInfoList().add(d2);
        index.getDocInfoList().add(d3);
        Long num = indexStorage.updateByKey(index.getIndexKey(), index);
        System.out.println(num);
    }

    @Test
    void findAllTest() {
        List<Index> all = indexStorage.findAll();
        for(Index index : all) {
            System.out.println(index);
        }
    }

    @Test
    void findByKey() {
        String key = "西瓜";
        List<Index> byKey = indexStorage.findByKey(key);
        for(Index index : byKey) {
            System.out.println(index);
        }
    }

    @Test
    void findFieldsTest() {
        Query query = new Query();
        Field fields = query.fields();
        fields.exclude("_id");
        fields.include("indexKey");
        List<Map> indices = mongoTemplate.find(query, Map.class, "word_temp");
        for(Map map : indices) {
            System.out.println(map.get("indexKey"));
        }
    }
}
