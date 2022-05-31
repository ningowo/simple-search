package team.snof.simplesearch.search.engine;

import org.springframework.beans.factory.annotation.Autowired;
import team.snof.simplesearch.common.util.SnowflakeIdGenerator;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.model.dao.*;
import team.snof.simplesearch.search.storage.DocStorage;
import team.snof.simplesearch.search.storage.MetaDataStorage;
import team.snof.simplesearch.search.storage.IndexPartialStorage;
import java.util.*;

public class DocParser {
    @Autowired
    WordSegmentation segmentation;
    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    DocStorage docStorage;
    @Autowired
    MetaDataStorage metaDataStorage;
    @Autowired
    IndexPartialStorage indexPartialStorage;


    // 解析doc，并获得索引所需参数
    public void parse(List<Doc> docList) {
        for (Doc doc : docList) {
            Long doc_id = snowflakeIdGenerator.generate();  // 调用雪花id生成doc_id 后面文档存储也要用这个id
            parseDoc(doc.getUrl(), doc.getCaption(), doc_id);
        }
    }

    // 解析每个doc中间参数存入word_temp表中
    public void parseDoc(String Url, String docCaption, long doc_id) {
        // 对文档分词
        List<String> wordList = segmentation.segment(docCaption);

        // 文档长度
        long doc_len = wordList.size();

        // 分词词频 <word, doc_id, word_freq>
        HashMap<String, Long> map = new HashMap<>();
        for (String word : wordList) {
            map.put(word, map.getOrDefault(word, 0L) + 1);
        }
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            IndexPartial indexPartial = new IndexPartial(entry.getKey(), doc_id, entry.getValue(), doc_len);
            indexPartialStorage.saveIndexPartial(indexPartial);
        }

        // 文档存储
        Doc doc = new Doc(doc_id, Url, docCaption);
        docStorage.saveDoc(doc);

        // 更新DocMetaData
        metaDataStorage.addDoc(doc_len);
    }
}
