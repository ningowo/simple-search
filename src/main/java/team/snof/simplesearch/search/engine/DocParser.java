package team.snof.simplesearch.search.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.CollectionSpliter;
import team.snof.simplesearch.common.util.SnowFlakeIDGenerator;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.model.dao.ForwardIndex;
import team.snof.simplesearch.search.model.dao.WordFreq;
import team.snof.simplesearch.search.model.dao.Doc;
import team.snof.simplesearch.search.storage.DocStorage;
import team.snof.simplesearch.search.storage.ForwardIndexStorage;
import team.snof.simplesearch.search.storage.InvertedIndexStorage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DocParser {

    @Autowired
    WordSegmentation wordSegmentation;

    @Autowired
    DocStorage docStorage;

    @Autowired
    ForwardIndexStorage forwardIndexStorage;

    @Autowired
    InvertedIndexStorage invertedIndexStorage;

    // 这里用CallerRunsPolicy阻塞主线程
    @Autowired
    private Executor docParserExecutor;

    /**
     * 解析doc，并获得索引所需参数
     */
    public long parseAndBuild(List<Doc> docList) {
        CollectionSpliter<Doc> spliter = new CollectionSpliter<>();
        List<List<Doc>> docLists = spliter.splitList(docList, 4000);

        CountDownLatch countDownLatch = new CountDownLatch(docLists.size());
        for (List<Doc> docs : docLists) {
            docParserExecutor.execute(() -> {
                try {
                    for (Doc doc : docs) {
                        // 1. 生成雪花id (这里暂时用雪花id，来不及改了，应该用mongodb自己生成的_id的）
                        String docId = String.valueOf(SnowFlakeIDGenerator.generateSnowFlakeId());
                        doc.setId(docId);
                        docStorage.addDoc(doc);

                        // 2. 解析获取分词在文档中词频
                        parseDoc(doc.getCaption(), docId);
                    }
                } finally {
                    // 防止线程内出错，锁无法释放
                    countDownLatch.countDown();
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return docList.size();
    }

    /**
     * 解析文档，构建索引，方便之后进行搜索
     */
    public void parseDoc(String caption, String docId) {
        // 1. 分词
        Map<String, Integer> wordToFreqMap;
        try {
            wordToFreqMap = wordSegmentation.segment(caption);
        } catch (IOException e) {
            log.error("分词失败：" + caption);
            return;
        }

        // 2. 构建倒排索引
        for (String word: wordToFreqMap.keySet()) {
            addToInvertedIndex(word, docId);
        }

        // 3. 构建正排索引
        long docLength = wordToFreqMap.size();
        List<WordFreq> wordFreqList = wordToFreqMap.entrySet().stream()
                .map(entry -> new WordFreq(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        buildForwardIndex(docId, docLength, wordFreqList);
    }

    private void addToInvertedIndex(String word, String docId) {
        invertedIndexStorage.save(word, docId);
    }

    private void buildForwardIndex(String docId, long docLength, List<WordFreq> wordFreqList) {
        forwardIndexStorage.save(new ForwardIndex(docId, docLength, wordFreqList));
    }

}
