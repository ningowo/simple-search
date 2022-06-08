package team.snof.simplesearch.search.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.doc.Doc4Sort;
import team.snof.simplesearch.search.model.dao.doc.DocInfo;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.dao.doc.Word4Sort;
import team.snof.simplesearch.search.storage.DocLenStorage;
import team.snof.simplesearch.search.storage.IndexPartialStorage;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
public  class SortLogic {

    @Autowired
    DocLenStorage docLenStorage;
    @Autowired
    IndexPartialStorage indexPartialStorage;
    @Autowired
    WordSegmentation segmentation;

    private static HashMap<String,BigDecimal> word2IDF = new HashMap<>();
    //private static final int relatedResultNum = 10; //相关检索数目
    private static final int relatedKeywordNum = 3; //每个相关搜索关联的关键字个数
    private static final double k_3 = 1.5;  // k3  1.2~2

    //文档排序
    public List<Long> docSort(List<Index> indexs, Map<String, Integer> wordToFreqMap) {
        //1.计算文档对应的相似度
        HashMap<Long, BigDecimal> doc2Similarity = new HashMap<>();//kv <docID,similarity>
        for (Index index : indexs) {
            String word = index.getIndexKey();
            for (DocInfo doc : index.getDocInfoList()) {
                BigDecimal corr = doc.getCorr().multiply(new BigDecimal((k_3 + 1) * wordToFreqMap.get(word)))
                        .divide(new BigDecimal(k_3 + wordToFreqMap.get(word)), 3, RoundingMode.HALF_EVEN);
                doc2Similarity.put(doc.getDocId(), doc2Similarity.getOrDefault(doc.getDocId(), new BigDecimal(0)).add(corr));
            }

        }

        //2.按相似度从高到低排序
        Doc4Sort[] docs = new Doc4Sort[doc2Similarity.size()];
        int idx = 0;
        for(Map.Entry<Long,BigDecimal> entry:doc2Similarity.entrySet()){
            docs[idx++] = new Doc4Sort(entry.getKey(),entry.getValue());
        }
        List<Long> orderedDocs = new ArrayList<>();//DocId
        Arrays.sort(docs,Collections.reverseOrder());

        for(Doc4Sort doc:docs){
            orderedDocs.add(doc.getDocId());
        }
        return orderedDocs;
    }

    // 相关搜索分词排序
    //!考虑到doc中的关键词可能会多次出现，目前返回关键词值前三大(期望得到主谓宾结构)的不同单词作为相关搜索
    public List<String> wordSort(List<Doc> docs, Map<String, Integer> wordToFreqMap) {
        List<String> relatedSearch = new ArrayList<>();
        for(Doc doc:docs){
            relatedSearch.add(calRelatedSearch(doc));
        }
        return relatedSearch;
    }

    // 计算单词的IDF
    private HashMap<String, BigDecimal> calIDF(long doc_num) {
        // 1. 统计包含某个分词的文档个数  <word,wordDocNum>
        List<String> wordListTotal = indexPartialStorage.getAllIndexPartialWord();
        HashMap<String, Long> word2FreqMap = indexPartialStorage.getWordDocNum(wordListTotal);

        // 2. 计算IDF  <word,IDF>
        HashMap<String, BigDecimal> word2IDF = new HashMap<>();
        for (String word : word2FreqMap.keySet()) {
            long wordDocNum = word2FreqMap.get(word);
            BigDecimal weight = BigDecimal.valueOf(Math.log((doc_num - wordDocNum + 0.5) / (wordDocNum + 0.5)));
            word2IDF.put(word, weight);
        }
        return word2IDF;
    }

    // 计算某一文档的词频
    private HashMap<String,BigDecimal> calTF(List<String> wordList){
        // <word,term frequency>
        HashMap<String,BigDecimal> word2Num = new HashMap<>();

        for (String word : wordList) {
            word2Num.put(word, word2Num.getOrDefault(word, BigDecimal.valueOf(0)).add(BigDecimal.valueOf(1)));
        }
        return word2Num;
    }

    private String calRelatedSearch(Doc doc) {

        //1.对文档分词,并得到词频
        Map<String, Integer> word2Num = null;
        try {
            word2Num = segmentation.segment(doc.getCaption());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        //2.判断IDF是否为空并计算IDF
        if(word2IDF.isEmpty()){
            long docNum = docLenStorage.getDocTotalNum();
            word2IDF = calIDF(docNum);
        }

        //4.根据TF-IDF计算关键字
        PriorityQueue<Word4Sort> topKeywords = new PriorityQueue<>();
        for(String word: word2Num.keySet()){
            if (word2IDF.containsKey(word)) {
                BigDecimal tf_idf = BigDecimal.valueOf(word2Num.get(word)).multiply(word2IDF.get(word));
                if(topKeywords.size() < relatedKeywordNum || tf_idf.compareTo(topKeywords.peek().getTf_idf()) > 0){
                    topKeywords.remove(topKeywords.peek());
                    topKeywords.add(new Word4Sort(word,tf_idf));
                }
            }
        }

        StringBuilder builder = new StringBuilder();
        for(Word4Sort word: topKeywords){
            builder.append(word.getWord());
        }

        return builder.toString();
    }
}
