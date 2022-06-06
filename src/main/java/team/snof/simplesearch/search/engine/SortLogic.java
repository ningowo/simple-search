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

import java.math.BigDecimal;
import java.util.*;

@Component
public  class SortLogic {
    @Autowired
    static EngineImpl engineImpl;
    @Autowired
    static DocLenStorage docLenStorage;
    @Autowired
    static IndexPartialStorage indexPartialStorage;
    @Autowired
    static WordSegmentation segmentation;

    private static HashMap<String,BigDecimal> word2IDF;
    //private static final int relatedResultNum = 10; //相关检索数目
    private static final int relatedKeywordNum = 3; //每个相关搜索关联的关键字个数
    private static final double k_3 = 1.5;  // k3  1.2~2

    //文档排序
    public static List<Long> docSort(List<Index> indexs, Map<String, Integer> wordToFreqMap) {
        //1.计算文档对应的相似度
        HashMap<Long, BigDecimal> doc2Similarity = new HashMap<>();//kv <docID,similarity>
        for (Index index : indexs) {
            String word = index.getIndexKey();
            for (DocInfo doc : index.getDocInfoList()) {
                BigDecimal corr = doc.getCorr().multiply(new BigDecimal((k_3 + 1) * wordToFreqMap.get(word)))
                        .divide(new BigDecimal(k_3 + wordToFreqMap.get(word)));
                //System.out.println(corr.toString());
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
            //System.out.printf("%d %f\n",doc.DocId,Double.valueOf(doc.similarity.toString()));
        }
        return orderedDocs;
    }

    // 相关搜索分词排序
    //!考虑到doc中的关键词可能会多次出现，目前返回关键词值前三大(期望得到主谓宾结构)的不同单词作为相关搜索
    public static List<String> wordSort(List<Doc> docs){
        List<String> relatedSearch = new ArrayList<>();
        for(Doc doc:docs){
            relatedSearch.add(calRelatedSearch(doc.getSnowflakeDocId()));
        }
        return relatedSearch;
    }

    // 计算单词的IDF
    private static HashMap<String, BigDecimal> calIDF(long doc_num) {
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
    private  static HashMap<String,BigDecimal> calTF(List<String> wordList){
        // <word,term frequency>
        HashMap<String,BigDecimal> word2Num = new HashMap<>();

        for (String word : wordList) {
            word2Num.put(word, word2Num.getOrDefault(word, BigDecimal.valueOf(0)).add(BigDecimal.valueOf(1)));
        }
        return word2Num;
    }

    private static String calRelatedSearch(Long docId){
        //1.对文档分词
        Doc doc = engineImpl.findDoc(docId);
        List<String> wordList = segmentation.segment(doc.getCaption());

        //2.判断IDF是否为空并计算IDF
        if(word2IDF.size() == 0){
            Long docNum = docLenStorage.getDocTotalNum();
            word2IDF = calIDF(docNum);
        }

        //3.计算TF
        HashMap<String,BigDecimal> word2Num = calTF(wordList);

        //4.根据TF-IDF计算关键字
        PriorityQueue<Word4Sort> topKeywords = new PriorityQueue<>();
        for(String word: wordList){
            BigDecimal tf_idf = word2Num.get(word).multiply(word2IDF.get(word));
            if(topKeywords.size() < relatedKeywordNum || tf_idf.compareTo(topKeywords.peek().getTf_idf()) == 1){
                topKeywords.remove(topKeywords.peek());
                topKeywords.add(new Word4Sort(word,tf_idf));
            }
        }

        String result = new String();
        for(Word4Sort word: topKeywords){
            result = result + word.getWord();
        }

        return result;
    }
}
