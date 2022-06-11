package team.snof.simplesearch.search.engine.index;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.CollectionSpliter;
import team.snof.simplesearch.common.util.SnowFlakeIDGenerator;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.storage.DocStorage;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.doc.DocLen;
import team.snof.simplesearch.search.model.dao.index.IndexPartial;
import team.snof.simplesearch.search.model.dao.index.TempData;
import team.snof.simplesearch.search.storage.DocLenStorage;
import team.snof.simplesearch.search.storage.IndexPartialStorage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Component
public class DocParser {

    @Autowired
    WordSegmentation wordSegmentation;

    @Autowired
    IndexPartialStorage indexPartialStorage;

    @Autowired
    DocLenStorage docLenStorage;

    @Autowired
    DocStorage docStorage;

    // 这里用CallerRunsPolicy阻塞主线程
    @Autowired
    private Executor docParserExecutor;

    // 解析doc，并获得索引所需参数
    public void parse(List<Doc> docList) {
        CollectionSpliter<Doc> spliter = new CollectionSpliter<>();
        List<List<Doc>> docLists = spliter.splitList(docList, 1000);

        CountDownLatch countDownLatch = new CountDownLatch(docLists.size());
        for (List<Doc> docs : docLists) {
//            docParserExecutor.execute(() -> {
                List<Doc> docsToParse = new ArrayList<>(docs.size());
                for (Doc doc : docs) {
                    // 1. 生成雪花id (这里暂时用雪花id，来不及改了，应该用mongodb自己生成的_id的）
                    String docId = String.valueOf(SnowFlakeIDGenerator.generateSnowFlakeId());
                    doc.setId(docId);

                    // todo 用完删
                    docsToParse.add(doc);

                    docStorage.addDoc(doc);
                }

                HashMap<String, Map<String, Integer>> map = new HashMap<>(docs.size());
                for (Doc doc : docsToParse) {
                    Map<String, Integer> wordToFreqMap = null;
                    String caption = doc.getCaption();
                    String docId = doc.getId();

                    try {
                        wordToFreqMap = wordSegmentation.segment(caption);
                        map.put(docId, wordToFreqMap);
                    } catch (IOException e) {
                        log.error("分词失败：" + caption);
                    }
                }

                log.info("开始存储doclen");
                for (String s : map.keySet()) {
                    Map<String, Integer> wordToFreqMap = map.get(s);
                    String docId = s;

                    // 文档长度
                    if (wordToFreqMap == null) {
                        return;
                    }
                    long docLength = wordToFreqMap.size();
                    // 储存文档长度到doc_length表  {doc_id, doc_len}
                    DocLen docLen = new DocLen(docId, docLength);
                    docLenStorage.save(docLen);
                }

                log.info("开始解析文档并存中间表");
                for (String s : map.keySet()) {
                    Map<String, Integer> wordToFreqMap = map.get(s);
                    String docId = s;
                    // 储存分词对应的文档和词频  <word, word_freq>
                    for (Map.Entry<String, Integer> entry : wordToFreqMap.entrySet()) {
                        TempData tempData = new TempData(docId, entry.getValue());
                        String word = entry.getKey();

                        IndexPartial indexPartial = new IndexPartial(word, tempData);
                        indexPartialStorage.saveIndexPartial(indexPartial);
                    }
                }

                log.info("一组读取完毕，数量：" + docs.size());
                countDownLatch.countDown();
//            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 解析每个doc中间参数存入word_temp表中
    public void parseDoc(String caption, String docId) {
        Map<String, Integer> wordToFreqMap = null;
        try {
            wordToFreqMap = wordSegmentation.segment(caption);
        } catch (IOException e) {
            log.error("分词失败：" + caption);
        }

        // 文档长度
        if (wordToFreqMap == null) {
            return;
        }
        long docLength = wordToFreqMap.size();
        // 储存文档长度到doc_length表  {doc_id, doc_len}
        DocLen docLen = new DocLen(docId, docLength);
        docLenStorage.save(docLen);

        // 储存分词对应的文档和词频  <word, word_freq>
        for (Map.Entry<String, Integer> entry : wordToFreqMap.entrySet()) {
            String word = entry.getKey();
            Integer freq = entry.getValue();

//            indexPartialStorage.saveIndexPartial(word, freq);
        }

    }
}
