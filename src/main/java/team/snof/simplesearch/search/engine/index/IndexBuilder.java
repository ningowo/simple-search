package team.snof.simplesearch.search.engine.index;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.CollectionSpliter;
import team.snof.simplesearch.search.model.dao.doc.DocInfo;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.dao.index.IndexPartial;
import team.snof.simplesearch.search.model.dao.index.TempData;
import team.snof.simplesearch.search.storage.DocLenStorage;
import team.snof.simplesearch.search.storage.IndexPartialStorage;
import team.snof.simplesearch.search.storage.IndexStorage;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Component
public class IndexBuilder {

    @Autowired
    IndexStorage indexStorage;

    @Autowired
    IndexPartialStorage indexPartialStorage;

    DocLenStorage docLenStorage;

    // 这里用CallerRunsPolicy阻塞主线程
    @Autowired
    private Executor taskExecutor;

    // BM25算法常量定义
    private final double k_1 = 1.5;  // k1可取1.2--2
    private final double b = 0.75;  // b取0.75

    // 计算文档平均长度与总文档数(全局变量 避免频繁查询）
    private static long docAveLen;
    private static long docTotalNum;

    @Autowired
    public void setDocLenStorage(DocLenStorage docLenStorage) {
        this.docLenStorage = docLenStorage;
        docAveLen = docLenStorage.getDocAveLen();
        docTotalNum = docLenStorage.getDocTotalNum();
    }

    /**
     * 根据中间表构建索引
     */
    public void buildIndexes() {
        log.info("开始读取wordtemp表...");
        // 读取word_temp中每条记录，计算word-doc关联度，得到(word, doc_id, corr)。存入word_doc_corr表中
        List<IndexPartial> allIndexPartialList = indexPartialStorage.getAllIndexPartials();
        log.info("读取wordtemp表完成：" + allIndexPartialList.size());

        // 1. 分词权重
        log.info("开始计算分词权重...");
        Map<String, Long> wordToAllDocsWordFreqMap = new HashMap<>();
        for (IndexPartial indexPartial : allIndexPartialList) {
            wordToAllDocsWordFreqMap.put(indexPartial.getIndexKey(), (long) indexPartial.getTempDataList().size());
        }

        Map<String, BigDecimal> wordWeightMap = calcWordsWeight(docTotalNum, wordToAllDocsWordFreqMap);
        log.info("分词权重计算完成, wordWeightMap.size():" + wordWeightMap.size());


        // 2.1 使用subList进行分割，不进行额外copy
        log.info("开始分割list...");
        CollectionSpliter<IndexPartial> spliter = new CollectionSpliter<>();

        List<List<IndexPartial>> indexPartialParts = spliter.splitList(allIndexPartialList, 300);
        log.info("分割list完成，list数量：" + indexPartialParts.size());

        // 2.2 多线程计算分词文档关联度，然后继续计算索引（权重*分词文档关联度），最后存储
        log.info("开始对wordtemp表里所有数据，计算索引...");
        CountDownLatch countDownLatch = new CountDownLatch(indexPartialParts.size());

        // 对每一组分词的，一组indexPartial
        for (List<IndexPartial> indexPartialPart : indexPartialParts) {
            taskExecutor.execute(() -> {
                calcIndexAndSave(indexPartialPart, wordWeightMap);
                countDownLatch.countDown();
                log.info("一组分词索引计算完成...");
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("对wordtemp表里所有数据，计算索引完成...");
    }

    private void calcIndexAndSave(List<IndexPartial> indexPartials, Map<String, BigDecimal> wordWeightMap) {
        // 新建一个列表存储这一批索引  然后批量存储到MongoDB中
        List<Index> indexList = new ArrayList<>(indexPartials.size());
        for (IndexPartial indexPartial : indexPartials) {
            String word = indexPartial.getIndexKey();
            List<TempData> tempDataList = indexPartial.getTempDataList();

            // 对出现这个分词的每一个文档
            List<DocInfo> docInfoList = new ArrayList<>(tempDataList.size());
            for (TempData tempData : tempDataList) {
                // 1. 计算分词与单个文档关联度
                BigDecimal wordDocCorr = calcWordDocCorr(tempData);

                // 2. 计算关联度（权重*分词文档关联度）
                BigDecimal wordWeight = wordWeightMap.get(word);
                BigDecimal corr = wordDocCorr.multiply(wordWeight);
                docInfoList.add(new DocInfo(tempData.getDocId(), corr));
            }

            // 3. 构建索引
            // docInfoList根据关联度corr 降序排序
//            docInfoList.sort((o1, o2) -> o2.getCorr().compareTo(o1.getCorr()));

            Index index = new Index(word, docInfoList);
            indexList.add(index);
        }

        // 批量存储索引
        indexStorage.saveBatch(indexList);
    }

    /**
     * 对word_temp的单条记录word-list计算关联度
     *
     * @param tempData
     * @return wordDocCorr
     */
    private BigDecimal calcWordDocCorr(TempData tempData) {
        long docId = tempData.getDocId();
        long docLen = docLenStorage.getDocLen(docId);
        int wordFreq = tempData.getWordFreq();

        // 计算分词文档关联度

        return BigDecimal.valueOf(tempData.getWordFreq())
                .multiply(BigDecimal.valueOf(k_1 + 1))
                .divide(BigDecimal.valueOf((k_1 * (1 - b)) + b * (double) (docLen / docAveLen) + wordFreq),
                        3, RoundingMode.HALF_EVEN);
    }

    /**
     * 对word_temp里所有分词计算分词权重
     * @param docTotalNum
     * @param wordToAllDocsWordFreqMap
     * @return
     */
    private Map<String, BigDecimal> calcWordsWeight(long docTotalNum, Map<String, Long> wordToAllDocsWordFreqMap) {
        Map<String, BigDecimal> wordWeightMap = new HashMap<>();
        for (String word : wordToAllDocsWordFreqMap.keySet()) {

            long AllDocsWordFreq = wordToAllDocsWordFreqMap.get(word);
            // 计算分词权重
            double mathLog = Math.log((docTotalNum - AllDocsWordFreq + 0.5) / (AllDocsWordFreq + 0.5));

            // double转BigDecimal时，要注意精度处理，不然浮点数表示时后面会有很长一串，比如2.230000000000000234这样
            BigDecimal wordWeight = new BigDecimal(mathLog, new MathContext(5, RoundingMode.HALF_EVEN));
            wordWeightMap.put(word, wordWeight);
        }
        return wordWeightMap;
    }
}
