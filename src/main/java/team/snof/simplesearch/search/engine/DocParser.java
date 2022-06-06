package team.snof.simplesearch.search.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.common.util.OssUtil;
import team.snof.simplesearch.common.util.SnowFlakeIDGenerator;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.doc.DocLen;
import team.snof.simplesearch.search.model.dao.index.IndexPartial;
import team.snof.simplesearch.search.model.dao.index.TempData;
import team.snof.simplesearch.search.storage.DocLenStorage;
import team.snof.simplesearch.search.storage.IndexPartialStorage;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class DocParser {
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 30;
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000, false));

    @Autowired
    WordSegmentation wordSegmentation;

    @Autowired
    SnowFlakeIDGenerator snowflakeIdGenerator;
  
    @Autowired
    IndexPartialStorage indexPartialStorage;
  
    @Autowired
    DocLenStorage docLenStorage;
  
    @Autowired
    OssUtil ossUtil;
 
  
    // 解析doc，并获得索引所需参数
    public void parse(List<Doc> docList) throws Exception{
        for (Doc doc : docList) {
            long docId = snowflakeIdGenerator.generateSnowFlakeId();  // 调用雪花id生成doc_id 后面文档存储也要用这个id
            parseDoc(doc.getUrl(), doc.getCaption(), docId);
        }
    }

    // 解析每个doc中间参数存入word_temp表中
    public void parseDoc(String url, String caption, long docId) throws Exception{
        /**
         * 分词接口返回的是map <word, word_freq>
         */
        // 分词在文档中词频
        Map<String, Integer> wordToFreqMap = wordSegmentation.segment(caption);

//      文档长度
        long docLength = wordToFreqMap.size();
        // 储存文档长度到doc_length表  {doc_id, doc_len}
        DocLen docLen = new DocLen(docId, docLength);
        docLenStorage.save(docLen);

        // 储存分词对应的文档和词频 {word: {doc_id, word_freq}}
        for (Map.Entry<String, Integer> entry : wordToFreqMap.entrySet()) {
            // 此处生成tempDatalist 传入构造方法
            TempData tempData = new TempData(docId, entry.getValue());
            List<TempData> tempDataList = new ArrayList<>();
            tempDataList.add(tempData);
            IndexPartial indexPartial = new IndexPartial(entry.getKey(), tempDataList);
            indexPartialStorage.saveIndexPartial(indexPartial);
        }

        // 储存文档
        Doc doc = new Doc(Long.valueOf(docId), url, caption);
        ossUtil.addDoc(doc);
    }
}
