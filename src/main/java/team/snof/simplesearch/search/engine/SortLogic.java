package team.snof.simplesearch.search.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.model.dao.Doc;
import team.snof.simplesearch.search.model.dao.ForwardIndex;
import team.snof.simplesearch.search.model.dao.InvertedIndex;
import team.snof.simplesearch.search.model.dao.WordFreq;
import team.snof.simplesearch.search.storage.DocStorage;
import team.snof.simplesearch.search.storage.ForwardIndexStorage;
import team.snof.simplesearch.search.storage.InvertedIndexStorage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public  class SortLogic {

    @Autowired
    WordSegmentation wordSegmentation;

    @Autowired
    DocStorage docStorage;

    @Autowired
    ForwardIndexStorage forwardIndexStorage;

    @Autowired
    InvertedIndexStorage invertedIndexStorage;

    // BM25算法常量定义
    // k1可取1.2--2
    private final double BM25_K1 = 1.5;
    // b取0.75
    private final double BM25_B = 0.75;
    // k3可取1.2~2
    private static final double BM25_K3 = 1.5;

    // 最多需要获取的相关搜索条数
    private static final int MAX_NUM_RELATED_SEARCH_TO_FIND = 8;


    /**
     * 计算单个分词对应的所有doc，与这个分词的关联度
     */
    public Map<String, Double> calcSingleWordCorr(String word, List<ForwardIndex> forwardIndices, long totalDocNum, double docAvgLen, Map<String, Integer> wordToFreqMap) {
        // key-docId, v-corr
        Map<String, Double> docCorrMap = new HashMap<>();

        // 计算文档对应的相似度
        // 1. 分词权重
        long allDocsWordFreq = forwardIndices.size();
        double wordWeight = Math.log((totalDocNum - allDocsWordFreq + 0.5) / (allDocsWordFreq + 0.5));

        for (ForwardIndex forwardIndex : forwardIndices) {
            // 2. 分词文档关联度
            long docLen = forwardIndex.getDocLength();
            int wordInDocFreq = forwardIndex.getWordFreqList().stream()
                    .filter(wordFreq1 -> wordFreq1.getWord().equals(word))
                    .findAny()
                    .map(WordFreq::getFreq).get();

            double wordDocCorr = wordInDocFreq * (BM25_K1 + 1) / ((BM25_K1 * (1 - BM25_B)) + BM25_B * docLen / docAvgLen + wordInDocFreq);

            // 3. 分词query关联度
            long wordInQueryFreq = wordToFreqMap.get(word);
            double wordQueryCorr = ((BM25_K3 + 1) * wordInQueryFreq) / (BM25_K3 + wordInQueryFreq);

            double totalCorr = wordWeight * wordDocCorr * wordQueryCorr;
            docCorrMap.put(forwardIndex.getDocId(), totalCorr);
        }

        return docCorrMap;
    }

    /**
     * 汇总每个doc的最终关联度
     */
    public List<String> sortAllDocs(List<Map<String, Double>> allDocCorrMapList) {
        // key-docId, value-汇总corr
        Map<String, Double> allDocCorrMap = new HashMap<>();

        // 直接把所有
        for (Map<String, Double> docCorrMap : allDocCorrMapList) {
            for (Map.Entry<String, Double> docCorr : docCorrMap.entrySet()) {
                String docId = docCorr.getKey();
                double curCorr = docCorr.getValue();

                Double oldCorr = allDocCorrMap.putIfAbsent(docId, curCorr);
                if (oldCorr != null) {
                    allDocCorrMap.put(docId, oldCorr + curCorr);
                }
            }
        }

        // 从大到小排序
        return allDocCorrMap.entrySet().stream()
                .sorted((o1, o2) -> {
                    if (o1.getValue() - o2.getValue() > 0.00001) {
                        return 1;
                    } else {
                        return 0;
                    }
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // 计算相关搜索  根据DocId
    public List<String> getRelatedSearchById(List<String> relatedSearchDocIds, Map<String, Integer> wordToFreqMap) {
        Set<String> relatedSearch = new HashSet<>();
        List<String> wordsToFindRelatedSearch = new ArrayList<>(wordToFreqMap.keySet());
        // 若只包含一个分词
        if (wordsToFindRelatedSearch.size() == 1) {
            String keyWord = wordsToFindRelatedSearch.get(0);
            for (String docId : relatedSearchDocIds) {
                String capation = docStorage.getDocById(docId).getCaption();
                String relatedWord = calRelatedSearch(capation, keyWord);
                if (!relatedWord.isBlank() && !relatedWord.equals(keyWord)) {
                    relatedSearch.add(relatedWord);
                }
                if (relatedSearch.size() == MAX_NUM_RELATED_SEARCH_TO_FIND) break;
            }
        } else {
            String keyWord_1 = "";
            String keyWord_2 = "";

            if (wordsToFindRelatedSearch.size() == 2) {  // 只包含两个分词
                keyWord_1 = wordsToFindRelatedSearch.get(0);
                keyWord_2 = wordsToFindRelatedSearch.get(1);
            } else {
                // 分词数量大于2：计算所有分词idf权重，选择最大的两个作为关键词
                HashMap<String, BigDecimal> wordToIDFMap = new HashMap<>();

                // 计算idf权重
                long docTotalNum = forwardIndexStorage.getTotalDocNum();
                for (String word : wordsToFindRelatedSearch) {
                    BigDecimal wordIDF = calWordIDF(word, docTotalNum);
                    wordToIDFMap.put(word, wordIDF);
                }

                // 选择idf权重最大的两个分词
                List<Map.Entry<String, BigDecimal>> wordToIDFList = new ArrayList<>(wordToIDFMap.entrySet());
                wordToIDFList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                keyWord_1 = wordToIDFList.get(0).getKey();
                keyWord_2 = wordToIDFList.get(1).getKey();
            }

            for (String docId : relatedSearchDocIds) {
                String capation = docStorage.getDocById(docId).getCaption();
                String relatedWord = calRelatedSearch(capation, keyWord_1, keyWord_2);
                if (!relatedWord.isBlank() && (!relatedWord.equals(keyWord_1) && !relatedWord.equals(keyWord_2))) {
                    relatedSearch.add(relatedWord);
                }
                if (relatedSearch.size() == MAX_NUM_RELATED_SEARCH_TO_FIND) break;
            }
        }
        log.info("构建相关搜索完成! " + "相关搜索: " + relatedSearch);
        return new ArrayList<>(relatedSearch);
    }

    // 计算相关搜索  根据Doc
    public List<String> getRelatedSearchByDoc(List<Doc> relatedSearchDocs, Map<String, Integer> wordToFreqMap) {
        Set<String> relatedSearch = new HashSet<>();
        List<String> wordsToFindRelatedSearch = new ArrayList<>(wordToFreqMap.keySet());
        // 若只包含一个分词
        if (wordsToFindRelatedSearch.size() == 1) {
            String keyWord = wordsToFindRelatedSearch.get(0);
            for (Doc doc : relatedSearchDocs) {
                String capation = doc.getCaption();
                String relatedWord = calRelatedSearch(capation, keyWord);
                if (!relatedWord.isBlank() && !relatedWord.equals(keyWord)) {
                    relatedSearch.add(relatedWord);
                }
                if (relatedSearch.size() == MAX_NUM_RELATED_SEARCH_TO_FIND) break;
            }
        } else { // 计算获取两个关键词
            String keyWord_1 = "";
            String keyWord_2 = "";

            if (wordsToFindRelatedSearch.size() == 2) {  // 只包含两个分词
                keyWord_1 = wordsToFindRelatedSearch.get(0);
                keyWord_2 = wordsToFindRelatedSearch.get(1);
            } else {
                // 分词数量大于2：计算所有分词idf权重，选择最大的两个作为关键词
                HashMap<String, BigDecimal> wordToIDFMap = new HashMap<>();

                // 计算idf权重
                long docTotalNum = forwardIndexStorage.getTotalDocNum();
                for (String word : wordsToFindRelatedSearch) {
                    BigDecimal wordIDF = calWordIDF(word, docTotalNum);
                    wordToIDFMap.put(word, wordIDF);
                }

                // 选择idf权重最大的两个分词
                List<Map.Entry<String, BigDecimal>> wordToIDFList = new ArrayList<>(wordToIDFMap.entrySet());
                wordToIDFList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                keyWord_1 = wordToIDFList.get(0).getKey();
                keyWord_2 = wordToIDFList.get(1).getKey();
            }

            for (Doc doc : relatedSearchDocs) {
                String capation = doc.getCaption();
                String relatedWord = calRelatedSearch(capation, keyWord_1, keyWord_2);
                if (!relatedWord.isBlank() && (!relatedWord.equals(keyWord_1) && !relatedWord.equals(keyWord_2))) {
                    relatedSearch.add(relatedWord);
                }
                if (relatedSearch.size() == MAX_NUM_RELATED_SEARCH_TO_FIND) break;
            }
        }
        log.info("构建相关搜索完成! " + "相关搜索: " + relatedSearch);
        return new ArrayList<>(relatedSearch);
    }

    /**
     * IDF算法计算单词权重
     * @param word
     * @param docTotalNum
     * @return
     */
    public BigDecimal calWordIDF(String word, long docTotalNum) {
        // 包含分词的文档数目
        InvertedIndex invertedIndex = invertedIndexStorage.find(word);
        if (invertedIndex == null) {
            return new BigDecimal(0);
        }
        long wordDocNum = invertedIndex.getDocIds().size();
        BigDecimal wordIDF = BigDecimal.valueOf(Math.log((docTotalNum - wordDocNum + 0.5) / (wordDocNum + 0.5)));
        return wordIDF;
    }

    /**
     * 对单个分词计算相关搜索
     * 这里是对一个文档 计算keyWord的相关搜索的逻辑
     */
    public String calRelatedSearch(String caption, String keyWord) {
        // 对文档分词
        List<String> wordList = null;
        try {
            wordList = wordSegmentation.segmentToWordList(caption);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String relatedQuery = "";
        // 如果keyWord是最后一个词语那么直接返回了
        for (int i = 0; i < wordList.size() - 1; i++) {
            String word = wordList.get(i);
            if (word.equals(keyWord)) {
                relatedQuery = word + wordList.get(i + 1);
                break;
            }
        }
        return relatedQuery;
    }

    /**
     * 对两个分词计算相关搜索
     * @param caption
     * @param keyWord_1
     * @param keyWord_2
     * @return
     */
    public String calRelatedSearch(String caption, String keyWord_1, String keyWord_2) {
        // 对文档分词
        List<String> wordList = null;
        try {
            wordList = wordSegmentation.segmentToWordList(caption);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String relatedQuery = "";
        // 如果keyWord是最后一个词语那么直接返回了
        for (int i = 0; i < wordList.size() - 1; i++) {
            String word = wordList.get(i);
            if (word.equals(keyWord_1) || word.equals(keyWord_2)) {
                relatedQuery = word + wordList.get(i + 1);
                break;
            }
        }
        return relatedQuery;
    }

}
