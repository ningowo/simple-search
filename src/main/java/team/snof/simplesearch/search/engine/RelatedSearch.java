package team.snof.simplesearch.search.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.engine.storage.IndexStorage;
import team.snof.simplesearch.search.model.dao.Word4Sort;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.index.DocInfo;
import team.snof.simplesearch.search.model.dao.doc.Doc4Sort;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.storage.IndexPartialStorage;
import team.snof.simplesearch.search.storage.MetaDataStorage;

import java.math.BigDecimal;
import java.util.*;

@SpringBootTest
public class RelatedSearch {
    @Autowired
    IndexStorage indexStorage;
    @Autowired
    MetaDataStorage metaDataStorage;
    @Autowired
    IndexPartialStorage indexPartialStorage;
    @Autowired
    IndexQuery indexQuery;
    @Autowired
    DocQuery docQuery;
    @Autowired
    WordSegmentation segmentation;

    private HashMap<String,BigDecimal> word2IDF;
    private static final int relatedResultNum = 10; //相关检索数目
    private static final int relatedKeywordNum = 3; //每个相关搜索关联的关键字个数
    private static final double k_3 = 1.5; //1.2~2

//    public static void main(String[] args){
//        List<String> words = new ArrayList<>();
//        words.add("test");
//        words.add("namomo");
//        searchRelated(words);
//    }

    //!考虑到doc中的关键词可能会多次出现，目前返回关键词值前三大(期望得到主谓宾结构)的不同单词作为相关搜索，
    public  List<List<String>> searchRelated(String query){
        List<String> words = new ArrayList<>();
        words = segmentation.segment(query);

        //1.计算分词在query出的出现次数
        HashMap<String, Long> word2TermFrequency = new HashMap<>();//  <word,term frequency>
        for (String word : words) {
            word2TermFrequency.put(word, word2TermFrequency.getOrDefault(word, 0L) + 1);
        }

        //2.获得索引
        List<Index> indexs = new ArrayList<>();
        for(String word: words){
            indexs.add(indexQuery.findIndexByKey(word));
        }

        //3.得到与query相似度最大的10个(至多10个)文档
        PriorityQueue<Doc4Sort> topRelevantDoc = calTopRelevantDoc(indexs,word2TermFrequency);

        //4.依据tf-idf算法得到文档的关键字，取前三大的关键字组为作为相关搜索
        List<List<String>> relatedSearch = new ArrayList<>();
        for(Doc4Sort doc: topRelevantDoc){
            relatedSearch.add(calRelatedSearch(doc.getDocId()));
        }

        return relatedSearch;
    }

    //计算与查询最相关的文档
    private PriorityQueue<Doc4Sort> calTopRelevantDoc(List<Index> indexs,HashMap<String, Long> word2TermFrequency){
        PriorityQueue<Doc4Sort> topRelevantDoc = new PriorityQueue<>();
        for(Index index:indexs){
            String word = index.getIndexKey();
            BigDecimal corr = new BigDecimal(0);
            for (DocInfo doc : index.getDocInfoList()) {
                corr.add(doc.getCorr().multiply(new BigDecimal((k_3 + 1) * word2TermFrequency.get(word)))
                        .divide(new BigDecimal(k_3 + word2TermFrequency.get(word))));
                if(topRelevantDoc.size() < relatedResultNum || corr.compareTo(topRelevantDoc.peek().getSimilarity()) == 1){
                    topRelevantDoc.remove(topRelevantDoc.peek());
                    topRelevantDoc.add(new Doc4Sort(doc.getDocId(),corr));
                }
            }
        }
        return topRelevantDoc;
    }

    // 计算单词的IDF
    private  HashMap<String, BigDecimal> calIDF(long doc_num) {
        // 1. 统计包含某个分词的文档个数  <word,wordDocNum>
        HashMap<String, Long> word2DocNum = indexPartialStorage.getWordDocNum();

        // 2. 计算IDF  <word,IDF>
        HashMap<String, BigDecimal> wordIDF = new HashMap<>();
        for (String word : word2DocNum.keySet()) {
            long wordDocNum = word2DocNum.get(word);
            BigDecimal weight = BigDecimal.valueOf(Math.log((doc_num - wordDocNum + 0.5) / (wordDocNum + 0.5)));
            wordIDF.put(word, weight);
        }
        return wordIDF;
    }

    // 计算某一文档的词频
    private  HashMap<String,BigDecimal> calTF(List<String> wordList){
        // <word,term frequency>
        HashMap<String,BigDecimal> word2Num = new HashMap<>();

        for (String word : wordList) {
            word2Num.put(word, word2Num.getOrDefault(word, BigDecimal.valueOf(0)).add(BigDecimal.valueOf(1)));
        }
        return word2Num;
    }

    private  List<String> calRelatedSearch(Long docId){
        //对文档分词
        Doc doc = docQuery.findDocById(docId);
        List<String> wordList = segmentation.segment(doc.getCaption());

        //计算IDF
        if(word2IDF.size() == 0){
            Long doc_num = metaDataStorage.getDocNum();
            word2IDF = calIDF(doc_num);
        }

        //计算TF
        HashMap<String,BigDecimal> word2Num = calTF(wordList);

        PriorityQueue<Word4Sort> topKeywords = new PriorityQueue<>();
        for(String word: wordList){
            BigDecimal tf_idf = word2Num.get(word).multiply(word2IDF.get(word));
            if(topKeywords.size() < relatedKeywordNum || tf_idf.compareTo(topKeywords.peek().getTf_idf()) == 1){
                topKeywords.remove(topKeywords.peek());
                topKeywords.add(new Word4Sort(word,tf_idf));
            }
        }

        List<String> keyWords = new ArrayList<>();
        for(Word4Sort word: topKeywords){
            keyWords.add(word.getWord());
        }

        return keyWords;
    }
}
