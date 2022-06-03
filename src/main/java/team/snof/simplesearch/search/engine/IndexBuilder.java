package team.snof.simplesearch.search.engine;

import org.springframework.beans.factory.annotation.Autowired;
import team.snof.simplesearch.search.model.dao.DocInfo;
import team.snof.simplesearch.search.model.dao.Index;
import team.snof.simplesearch.search.model.dao.IndexPartial;
import team.snof.simplesearch.search.model.dao.TempData;
import team.snof.simplesearch.search.storage.IndexPartialStorage;
import team.snof.simplesearch.search.storage.MetaDataStorage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IndexBuilder {
    @Autowired
    IndexStorage indexStorage;
    @Autowired
    MetaDataStorage metaDataStorage;
    @Autowired
    IndexPartialStorage indexPartialStorage;

    // BM25算法常量定义
    private final double k_1 = 1.5;  // k1可取1.2--2
    private final double b = 0.75;  // b取0.75


    // 根据中间表构建索引
    public void buildIndexes() {
        // 1. 计算文档平均长度与总文档数
        long doc_ave_len = metaDataStorage.getAvgLen();
        long doc_num = metaDataStorage.getDocNum();

        // 2. 分词权重
        HashMap<String, BigDecimal> wordWeightMap = calculateWeight(doc_num);

        // 3. 读取word_temp中每条记录 计算word-doc关联度 得到(word, doc_id, corr) 存入word_doc_corr表中
        // 现在直接生成list后  调用index存储即可
        long recordNum = indexPartialStorage.getIndexPartialNum();
        // word_temp的每一条记录 即每一个word
        for (long i = 1; i <= recordNum; i++) {
            String word = indexPartialStorage.getIndexPartialWord();
            IndexPartial indexPartial = indexPartialStorage.getIndexPartial(word);

            // 每条记录内部 又需要对list中的每一项计算corr 并生成索引的docInfoList
            List<DocInfo> docInfoList = new ArrayList<>();
            for (TempData tempData : indexPartial.tempDataList){
                BigDecimal corr = calculateCorr(word, wordWeightMap, tempData);
                DocInfo docInfo = new DocInfo(tempData.getDoc_id(), tempData.getWordFreq(), corr);
                docInfoList.add(docInfo);
            }

            Index index = new Index(word, docInfoList);
            indexStorage.saveIndex(index);
        }
    }

    //  读取排好序的word_doc_corr 实现对单个索引的构建
    public Index buildIndex(String word) {
        List<DocInfo> docIdAndCorrList = new ArrayList<>();
        return new Index(word, docIdAndCorrList);
    }

    // 对word_temp的单条记录word-list计算关联度
    public BigDecimal calculateCorr(String word, HashMap<String, BigDecimal> wordWeightMap, TempData tempData) {
        // corr = 分词权重 * 分词文档关联度
        long doc_id = tempData.getDoc_id();
        long word_freq = tempData.getWordFreq();
        long doc_len = indexPartialStorage.getDocLen(word, doc_id);
        long doc_ave_len = metaDataStorage.getDocAveLen();

        // 1. 分词权重 = weightMap.get(word)
        BigDecimal wordWeight = wordWeightMap.get(word);

        // 2. 分词文档关联度
        BigDecimal corr = wordWeight.multiply(BigDecimal.valueOf(word_freq).multiply(BigDecimal.valueOf(k_1+1)).divide
                (BigDecimal.valueOf((k_1*(1-b)) + b*(doc_len/doc_ave_len) + word_freq))) ;

        return corr;
    }

    // 计算所有word的权重
    public HashMap<String, BigDecimal> calculateWeight(long doc_num) {
        // 1. 统计包含某个分词的文档个数 word,wordDocNum
        HashMap<String, Long> wordDocNumMap = indexPartialStorage.getWordDocNum();

        // 2. 计算权重
        HashMap<String, BigDecimal> wordWeightMap = new HashMap<>();
        for (String word : wordDocNumMap.keySet()) {
            long wordDocNum = wordDocNumMap.get(word);
            BigDecimal wordWeight = BigDecimal.valueOf(Math.log((doc_num - wordDocNum + 0.5) / (wordDocNum + 0.5)));
            wordWeightMap.put(word, wordWeight);
        }
        return wordWeightMap;
    }
}
