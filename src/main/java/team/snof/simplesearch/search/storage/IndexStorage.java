package team.snof.simplesearch.search.storage;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.doc.DocInfo;
import team.snof.simplesearch.search.model.dao.index.Index;

import java.util.List;

@Slf4j
@Component
public class IndexStorage {
    @Autowired
    MongoTemplate mongoTemplate;

    public List<Index> findAll() {
        return mongoTemplate.findAll(Index.class, "word_docid_corr");
    }

    public List<Index> findByKey(String key) {
        Query query = new Query(Criteria.where("indexKey").is(key));
        return mongoTemplate.find(query, Index.class, "word_docid_corr");
    }

    public void save(Index index) {
        mongoTemplate.save(index, "word_docid_corr");
    }

    public void saveBatch(List<Index> indices) {
        for (Index index : indices) {
            Query query = new Query(Criteria.where("indexKey").is(index.getIndexKey()));
            Index oldIndex = mongoTemplate.findOne(query, Index.class, "word_docid_corr");
            if (oldIndex == null) {
                mongoTemplate.save(index, "word_docid_corr");
            } else {
                List<DocInfo> docInfoList = index.getDocInfoList();
                oldIndex.getDocInfoList().addAll(docInfoList);
                updateByKey(oldIndex.getIndexKey(), oldIndex);
            }
        }
    }

    public Long deleteByKey(String key) {
        Query query = new Query(Criteria.where("indexKey").is(key));
        DeleteResult remove = mongoTemplate.remove(query, Index.class, "word_docid_corr");
        return remove.getDeletedCount();
    }

    public Long updateByKey(String key, Index index) {
        Query query = new Query(Criteria.where("indexKey").is(key));
        Update update = new Update();
        update.set("docInfoList", index.getDocInfoList());
        UpdateResult result = mongoTemplate.updateFirst(query, update, Index.class, "word_docid_corr");
        return result.getModifiedCount();
    }
}