package team.snof.simplesearch.search.engine;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import team.snof.simplesearch.common.util.CSVFileReader;
import team.snof.simplesearch.common.util.SnowflakeIdGenerator;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.model.dao.BM25Parameter;
import team.snof.simplesearch.search.model.dao.Doc;
import team.snof.simplesearch.search.model.dao.Index;
import team.snof.simplesearch.search.service.BM25ParameterService;
import team.snof.simplesearch.search.service.DocLenService;
import team.snof.simplesearch.search.service.WordDocCorrService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocParser {
    @Autowired
    WordSegmentation segmentation;
    @Autowired
    DocLenService docLenService;
    @Autowired
    BM25ParameterService bm25ParameterService;
    @Autowired
    WordDocCorrService wordDocCorrService;
    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;
    @Autowired
    IndexStorage indexStorage;
    @Autowired
    DocStorage docStorage;

    // BM25算法常量定义
    private final double k_1 = 1.5;  // k1, k3可取1.2--2
    private final double k_3 = 1.5;
    private final double b = 0.75;  // b取0.75

    public static void main(String[] args) {
//        TODO 大数据量的分批操作？？?
        // 从csv文件获取Doc
        /**
         * 读取文档有两种方式：
         * 1. 读取csv 则在此处实现doc_id生成与文件存储
         * 2. 文档数据库已生成好 直接读取文档数据库 则直接调用文档数据库的文档及其doc_id 且不涉及文档存储操作
         */
        List<Doc> docList = CSVFileReader.readDocsFromCSV("");

        // 解析文件和存储文件
        DocParser docParser = new DocParser();
        docParser.parse(docList);

        // 构建索引并存储
        docParser.buildIndexes();
    }

    // 解析doc，并获得索引所需参数
    private void parse(List<Doc> docList) {
        for (Doc doc : docList) {
            Long doc_id = snowflakeIdGenerator.generate();  // 调用雪花id生成doc_id 后面文档存储也要用这个id
            parseDoc(doc.getCaption(), doc_id);
        }
    }

    // 解析每个doc中间参数存入表中
    public void parseDoc(String docCaption, long doc_id) {
        // 对文档分词
        List<String> wordList = segmentation.segment(docCaption);

        // 文档长度
        long doc_len = wordList.size();
        docLenService.saveDocLen(doc_id, doc_len);

        // 分词词频 <word, doc_id, word_freq>
        HashMap<String, Long> map = new HashMap<>();
        for (String word : wordList) {
            map.put(word, map.getOrDefault(word, 0L) + 1);
        }
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            bm25ParameterService.saveParameter(entry.getKey(), doc_id, entry.getValue());
        }
    }

    // 根据中间表构建索引
    public void buildIndexes() {
        // 1. 计算文档平均长度与总文档数
        long doc_ave_len = docLenService.getDocAveLen();
        long doc_num = docLenService.getDocNum();

        // 2. 分词权重
        HashMap<String, BigDecimal> wordWeightMap = calculateWeight(doc_num);

        // 3. 读取word_temp中每条记录 计算word-doc关联度 得到(word, doc_id, corr) 存入word_doc_corr表中
        long recordNum = bm25ParameterService.getRecordNum();
        for (long i = 1; i <= recordNum; i++) {
            BM25Parameter bm25Parameter = bm25ParameterService.getRecord(i);
            String word = bm25Parameter.getWord();
            long doc_id = bm25Parameter.getDoc_id();
            BigDecimal corr = calculateCorr(wordWeightMap, bm25Parameter);
            wordDocCorrService.saveWordDocCorr(word, doc_id, corr);
        }

        // 对word_doc_corr按照corr由高到低排序
        wordDocCorrService.sortWordDocCorr();

        // 4. 汇总同一个分词的数据 生成索引并存储
        // TODO 得到 List<Pair<Long, BigDecimal>>遍历太低效？ 再想一下具体怎么实现  换MangoDB??
        /**
         * 伪代码
         * for (record : word_doc_corr) {
         *     Index index = buildIndex(word);
         *     indexStorage.saveIndex(index);
         */
    }

    //  读取排好序的word_doc_corr 实现对单个索引的构建
    public Index buildIndex(String word) {
        List<Pair<Long, BigDecimal>> docIdAndCorrList = new ArrayList<>();
        return new Index(word, docIdAndCorrList);
    }

    // 对word_temp的单条记录计算关联度
    public BigDecimal calculateCorr(HashMap<String, BigDecimal> wordWeightMap, BM25Parameter bm25Parameter) {
        // corr = 分词权重 * 分词文档关联度
        String word = bm25Parameter.getWord();
        long doc_id = bm25Parameter.getDoc_id();
        long word_freq = bm25Parameter.getWord_freq();
        long doc_len = docLenService.getDocLen(doc_id);
        long doc_ave_len = docLenService.getDocAveLen();

        // 1. 分词权重 = weightMap.get(word)
        BigDecimal wordWeight = wordWeightMap.get(word);

        // 2. 分词文档关联度
        BigDecimal corr = BigDecimal.valueOf(word_freq).multiply(BigDecimal.valueOf(k_1+1)).divide
                (BigDecimal.valueOf((k_1*(1-b)) + b*(doc_len/doc_ave_len) + word_freq)) ;

        return corr;
    }

    // 计算所有word的权重
    public HashMap<String, BigDecimal> calculateWeight(long doc_num) {
        // 1. 统计包含某个分词的文档个数 word,wordDocNum
        HashMap<String, Long> wordDocNumMap = bm25ParameterService.getWordDocNum();

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
