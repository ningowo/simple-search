package team.snof.simplesearch.search.engine.storage;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.IndexPartial;
import team.snof.simplesearch.search.model.dao.index.Index;

import java.util.List;

@Slf4j
@Component
public class IndexStorage {
    @Autowired
    MongoTemplate mongoTemplate;

    public List<Index> findAll() {
        return mongoTemplate.findAll(Index.class);
    }

    public List<Index> findByKey(String key) {
        Query query = new Query(Criteria.where("indexKey").is(key));
        return mongoTemplate.find(query, Index.class);
    }

    public void save(Index index) {
        mongoTemplate.save(index);
    }

    public void save(List<Index> indices) {
        for(Index index : indices) {
            save(index);
        }
    }

    public Long deleteByKey(String key) {
        Query query = new Query(Criteria.where("indexKey").is(key));
        DeleteResult remove = mongoTemplate.remove(query, Index.class);
        return remove.getDeletedCount();
    }

    public Long updateByKey(String key, Index index) {
        Query query = new Query(Criteria.where("indexKey").is(key));
        Update update = new Update();
        update.set("docInfoList", index.getDocInfoList());
        UpdateResult result = mongoTemplate.updateFirst(query, update, Index.class);
        return result.getModifiedCount();
    }

    public void saveIndexPartial(IndexPartial indexPartial) {
        try {
            // 调用云存储或者本地存储
        } catch (Exception e) {
            log.error("");
        }

    }
}
