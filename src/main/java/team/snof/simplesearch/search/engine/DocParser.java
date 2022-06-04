package team.snof.simplesearch.search.engine;

import org.springframework.beans.factory.annotation.Autowired;
import team.snof.simplesearch.common.util.SnowflakeIdGenerator;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.model.bo.BM25ParseDocResult;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.doc.DocLen;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.dao.index.IndexPartial;
import team.snof.simplesearch.search.storage.DocLenStorage;
import team.snof.simplesearch.search.storage.DocStorage;
import team.snof.simplesearch.search.storage.IndexPartialStorage;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    WordSegmentation segmentation;
    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    DocStorage docStorage;
    @Autowired
    IndexPartialStorage indexPartialStorage;
    @Autowired
    DocLenStorage docLenStorage;

    // 解析doc，并获得索引所需参数
    public void parse(List<Doc> docList) {
        for (Doc doc : docList) {
            long docId = snowflakeIdGenerator.generate();  // 调用雪花id生成doc_id 后面文档存储也要用这个id
            parseDoc(doc.getUrl(), doc.getCaption(), docId);
        }
    }

    // 解析每个doc中间参数存入word_temp表中
    public void parseDoc(String url, String caption, long docId) {
        // 对文档分词
        List<String> wordList = segmentation.segment(caption);

        // 文档长度
        long docLength = wordList.size();
        // 储存文档长度到doc_length表  {doc_id, doc_len}
        DocLen docLen = new DocLen(docId, docLength);
        docLenStorage.save(docLen);

        // 分词在文档中词频
        HashMap<String, Long> wordToFreqMap = new HashMap<>();
        for (String word : wordList) {
            wordToFreqMap.put(word, wordToFreqMap.getOrDefault(word, 0L) + 1);
        }
        // 储存分词对应的文档和词频 {word: {doc_id, word_freq}}
        for (Map.Entry<String, Long> entry : wordToFreqMap.entrySet()) {
            IndexPartial indexPartial = new IndexPartial(entry.getKey(), docId, entry.getValue());
            indexPartialStorage.saveIndexPartial(indexPartial);
        }
    }

    public List<Index> parseDoc(String docCaption) {
        BM25ParseDocResult bm25ParseDocResult = BM25ParseDocResult.builder().build();

        // 分词接口
        List<String> terms = segmentation.segment(docCaption);


        return null;
    }
}
