package team.snof.simplesearch.search.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.dao.index.IndexPartial;
import team.snof.simplesearch.search.model.dao.index.TempData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class IndexPartialStorage {

    @Autowired
    MongoTemplate mongoTemplate;

    public List<IndexPartial> getAllIndexPartials() {
        return mongoTemplate.findAll(IndexPartial.class, "word_temp");
    }

    public IndexPartial getIndexPartial(String word) {
        Query query = new Query().addCriteria(Criteria.where("indexKey").is(word));
        return mongoTemplate.findOne(query, IndexPartial.class, "word_temp");
    }

    // 若不存在word记录 则新增存储  若存在则读word对应的list进行扩充更新
    public void saveIndexPartial(IndexPartial indexPartial) {
        Query query = new Query().addCriteria(Criteria.where("indexKey").is(indexPartial.getIndexKey()));
        IndexPartial wordIndexPartial = mongoTemplate.findOne(query, IndexPartial.class, "word_temp");
        if (wordIndexPartial == null) {
            mongoTemplate.save(indexPartial, "word_temp");
        } else {
            List<TempData> tempDataList = indexPartial.getTempDataList();
            wordIndexPartial.getTempDataList().addAll(tempDataList);
            Update update = new Update();
            update.set("tempDataList", wordIndexPartial.getTempDataList());
            mongoTemplate.updateFirst(query, update, IndexPartial.class, "word_temp");
        }
    }
}