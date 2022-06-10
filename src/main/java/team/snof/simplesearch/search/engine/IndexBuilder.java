package team.snof.simplesearch.search.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.CollectionSpliter;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.doc.DocInfo;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.dao.index.IndexPartial;
import team.snof.simplesearch.search.model.dao.index.TempData;
import team.snof.simplesearch.search.storage.DocLenStorage;
import team.snof.simplesearch.search.storage.IndexPartialStorage;
import team.snof.simplesearch.search.storage.IndexStorage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class IndexBuilder {

    @Autowired
    IndexStorage indexStorage;
    @Autowired
    IndexPartialStorage indexPartialStorage;
    @Autowired
    DocLenStorage docLenStorage;

//    ThreadPoolExecutor executor;

    // BM25算法常量定义
    private final double k_1 = 1.5;  // k1可取1.2--2
    private final double b = 0.75;  // b取0.75

    // 计算文档平均长度与总文档数(全局变量 避免频繁查询）
    long docAveLen;
    long docTotalNum;

//    todo 多线程
//    public IndexBuilder() {
////        executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 5, 30,
////                TimeUnit.SECONDS, new ArrayBlockingQueue<>(100, false));
//
//    }

    public void setDocLenStorage(DocLenStorage docLenStorage) {
        this.docLenStorage = docLenStorage;
        docAveLen = docLenStorage.getDocAveLen();
        docTotalNum = docLenStorage.getDocTotalNum();
    }

    // 根据中间表构建索引
    public void buildIndexes() {
        // 读取word_temp中每条记录，计算word-doc关联度，得到(word, doc_id, corr)。存入word_doc_corr表中
        List<String> wordListTotal = indexPartialStorage.getAllIndexPartialWord();

        // 分词权重
        HashMap<String, BigDecimal> wordWeightMap = calculateWeight(docTotalNum, wordListTotal);

        // 对word_temp的部分记录，即部分word遍历读取，避免占用过多内存
        CollectionSpliter<String> spliter = new CollectionSpliter<>();
        List<List<String>> wordlistParts = spliter.splitList(wordListTotal, 200);

        // 多线程进行解析和存储
        for (List<String> wordList : wordlistParts) {
//            executor.execute(() -> calcIndexAndSave(wordList, wordWeightMap));
            calcIndexAndSave(wordList, wordWeightMap);
        }
    }

    private void calcIndexAndSave(List<String> wordList, HashMap<String, BigDecimal> wordWeightMap) {
        // 新建一个列表存储这一批索引  然后批量存储到MongoDB中
        List<Index> indexList = new ArrayList<>();
        for (String word : wordList) {
            IndexPartial indexPartial = indexPartialStorage.getIndexPartial(word);

            // 每条记录内部需要对list中的每一项计算corr，并生成索引的docInfoList
            List<DocInfo> docInfoList = new ArrayList<>();
            for (TempData tempData : indexPartial.tempDataList) {
                BigDecimal corr = calculateCorr(word, wordWeightMap, tempData);
                DocInfo docInfo = new DocInfo(tempData.getDocId(), corr);
                docInfoList.add(docInfo);
            }

            // docInfoList根据关联度corr 降序排序
            Collections.sort(docInfoList, new Comparator<DocInfo>() {
                @Override
                public int compare(DocInfo o1, DocInfo o2) {
                    return o2.getCorr().compareTo(o1.getCorr());
                }
            });

            Index index = new Index(word, docInfoList);
            indexList.add(index);
        }

        // MongoDB层实现一下
        indexStorage.saveBatch(indexList);
    }

    /**
     * 对word_temp的单条记录word-list计算关联度
     * corr = 分词权重 * 分词文档关联度
     *
     * @param word
     * @param wordWeightMap
     * @param tempData
     * @return
     */
    public BigDecimal calculateCorr(String word, HashMap<String, BigDecimal> wordWeightMap, TempData tempData) {
        long docId = tempData.getDocId();
        long wordFreq = tempData.getWordFreq();
        long docLen = docLenStorage.getDocLen(docId);

        // 1. 分词权重
        BigDecimal wordWeight = wordWeightMap.get(word);

        // 2. 分词文档关联度
        BigDecimal corr = wordWeight
                .multiply(BigDecimal.valueOf(wordFreq)
                        .multiply(BigDecimal.valueOf(k_1 + 1))
                        .divide(BigDecimal.valueOf((k_1 * (1 - b)) + b * (double) (docLen / docAveLen) + wordFreq),
                                3, RoundingMode.HALF_EVEN));

        return corr;
    }

    public HashMap<String, BigDecimal> calculateWeight(long docTotalNum, List<String> wordListTotal) {
        // 1. 统计包含某个分词的文档个数 word,wordDocNum

        // 计算所有word的权重
        HashMap<String, Long> wordDocNumMap = indexPartialStorage.getWordDocNum(wordListTotal);

        // 2. 计算权重
        HashMap<String, BigDecimal> wordWeightMap = new HashMap<>();
        for (String word : wordDocNumMap.keySet()) {
            long wordDocNum = wordDocNumMap.get(word);
            double log = Math.log((docTotalNum - wordDocNum + 0.5) / (wordDocNum + 0.5));
//            System.out.println("=============" + log);
            BigDecimal wordWeight = BigDecimal.valueOf(log);
            wordWeightMap.put(word, wordWeight);
        }
        return wordWeightMap;
    }
}
