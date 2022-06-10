package team.snof.simplesearch.search.storage;

import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.dao.index.IndexPartial;
import team.snof.simplesearch.search.model.dao.index.TempData;

import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class IndexPartialStorage {

    @Autowired
    MongoTemplate mongoTemplate;

    public List<String> getAllIndexPartialWord() {
        // 优化mongoDB操作？？ 这样写好蠢啊
        List<String> indexPartialWordList = new ArrayList<>();
        List<IndexPartial> indexPartials = mongoTemplate.findAll(IndexPartial.class, "word_temp");
        for (IndexPartial indexPartial : indexPartials) {
            indexPartialWordList.add(indexPartial.getIndexKey());
        }
        return indexPartialWordList;
    }

    public IndexPartial getIndexPartial(String word) {
        Query query = new Query().addCriteria(Criteria.where("indexKey").is(word));
        return mongoTemplate.findOne(query, IndexPartial.class, "word_temp");
    }

    // <word, word对应doc数量>
    public HashMap<String, Long> getWordDocNum(List<String> wordListTotal) {
        HashMap<String, Long> wordDocNumMap = new HashMap<>();
        for (String word : wordListTotal) {
            Query query = new Query().addCriteria(Criteria.where("indexKey").is(word));
            long wordDocNum = mongoTemplate.findOne(query, IndexPartial.class, "word_temp").getTempDataList().size();
            wordDocNumMap.put(word, wordDocNum);
        }
        return wordDocNumMap;
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