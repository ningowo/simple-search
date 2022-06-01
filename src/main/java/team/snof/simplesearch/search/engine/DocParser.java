package team.snof.simplesearch.search.engine;

import org.springframework.beans.factory.annotation.Autowired;
import team.snof.simplesearch.common.util.SnowflakeIdGenerator;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.index.IndexPartial;
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
    public void parseDoc(String url, String caption, long docId) {
        // 对文档分词
        List<String> wordList = segmentation.segment(caption);

        // 文档长度
        long doc_len = wordList.size();

        // 这里最好写清map的内容
        HashMap<String, Long> wordToFreqMap = new HashMap<>();
        // 统计分词在文档中词频
        for (String word : wordList) {
            wordToFreqMap.put(word, wordToFreqMap.getOrDefault(word, 0L) + 1);
        }
        // 储存分词对应的文档和词频
        for (Map.Entry<String, Long> entry : wordToFreqMap.entrySet()) {
            // MongoDB里是结构是{word: {doc_id, doc_len, word_freq}}，在indexPartialStorage实现此逻辑
            IndexPartial indexPartial = new IndexPartial(entry.getKey(), docId, doc_len, entry.getValue());
            indexPartialStorage.saveIndexPartial(indexPartial);
        }

        // 储存文档
        Doc doc = new Doc(docId, url, caption);
        docStorage.saveDoc(doc);

        // 更新DocMetaData
        metaDataStorage.addDoc(doc_len);
    }
}
