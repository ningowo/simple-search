package team.snof.simplesearch.search.engine;

import io.swagger.models.auth.In;
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
    WordSegmentation wordSegmentation;

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
        Arrays.sort(docs,Collections.reverseOrder());  // 内部改写了compareTo方法 未加@Override

        for(Doc4Sort doc:docs){
            orderedDocs.add(doc.getDocId());
        }
        return orderedDocs;  // list(DocId)  ordered
    }

    /**
     * 修改相关搜索实现方式
     * @param docs
     * @param wordToFreqMap
     * @return
     */
    public List<String> wordSort(List<Doc> docs, Map<String, Integer> wordToFreqMap) throws IOException {
        // 先得到query的关键词  计算wordToFreqMap中所有word的IDF  最大的作为关键词
        long docTotalNum = docLenStorage.getDocTotalNum();
        HashMap<String, BigDecimal> wordToIDFMap = new HashMap<>();
        String keyWord = wordToFreqMap.keySet().iterator().next();
        BigDecimal keyIDF = calWordIDF(keyWord, docTotalNum);

        for (String word : wordToFreqMap.keySet()) {
            BigDecimal wordIDF = calWordIDF(word, docTotalNum);
            if (wordIDF.compareTo(keyIDF) == 1) keyWord = word;
        }

        // 取docs前4个文档进行解析  获取关键词和其后一个位置的词语 拼接称为相关搜索词语
        List<String> relatedSearch = new ArrayList<>();
        int maxNum = Math.min(4, docs.size());
        for (int i = 0; i < maxNum; i++) {
            relatedSearch.add(calRelatedSearch(docs.get(i).getCaption(), keyWord));
        }
        return relatedSearch;
    }

    // 计算单个分词的IDF  （未考虑单词与query关联度）
    public BigDecimal calWordIDF(String word, long docTotalNum) {
        // 包含分词的文档数目
        long wordDocNum = indexPartialStorage.getIndexPartial(word).getTempDataList().size();
        BigDecimal wordIDF = BigDecimal.valueOf(Math.log((docTotalNum - wordDocNum + 0.5) / (wordDocNum + 0.5)));
        return wordIDF;
    }

    public String calRelatedSearch(String caption, String keyWord) throws IOException {
        // 对文档分词
        List<String> wordList = wordSegmentation.segmentToWordList(caption);
        String relatedQuery = keyWord;
        // 如果keyWord是最后一个词语那么直接返回了
        for (int i = 0; i < wordList.size() - 1; i++) {
            String word = wordList.get(i);
            if (word.equals(keyWord)) {
                relatedQuery += wordList.get(i + 1);
                break;
            }
        }
        return relatedQuery;
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
}
