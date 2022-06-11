package team.snof.simplesearch.search.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.bo.Doc4Sort;
import team.snof.simplesearch.search.model.dao.index.DocInfo;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.dao.index.IndexPartial;
import team.snof.simplesearch.search.storage.DocLenStorage;
import team.snof.simplesearch.search.storage.IndexPartialStorage;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Component
public  class SortLogic {

    @Autowired
    DocLenStorage docLenStorage;

    @Autowired
    IndexPartialStorage indexPartialStorage;

    @Autowired
    WordSegmentation wordSegmentation;

    // 最多需要获取的相关搜索条数
    private static final int MAX_NUM_RELATED_SEARCH_TO_FIND = 8;

    // 最多进行相关搜索检索的文档
    private static final int MAX_DOC_NUM_TO_PARSE_RELATED_SEARCH = 6;

    // BM25参数 k3  1.2~2
    private static final double k_3 = 1.5;

    /**
     * 文档排序逻辑
     * @param indexs
     * @param wordToFreqMap
     * @return 已排序文档ids
     */
    public List<String> docSort(List<Index> indexs, Map<String, Integer> wordToFreqMap) {
        //1.计算文档对应的相似度
        HashMap<String, BigDecimal> docId2Similarity = new HashMap<>();
        for (Index index : indexs) {
            String word = index.getIndexKey();
            for (DocInfo doc : index.getDocInfoList()) {
                BigDecimal corr = doc.getCorr().multiply(new BigDecimal((k_3 + 1) * wordToFreqMap.get(word)))
                        .divide(new BigDecimal(k_3 + wordToFreqMap.get(word)), 3, RoundingMode.HALF_EVEN);
                docId2Similarity.put(doc.getDocId(), docId2Similarity.getOrDefault(doc.getDocId(), new BigDecimal(0)).add(corr));
            }
        }

        //2.按相似度从高到低排序
        Doc4Sort[] docs = new Doc4Sort[docId2Similarity.size()];
        int idx = 0;
        for(Map.Entry<String,BigDecimal> entry:docId2Similarity.entrySet()){
            docs[idx++] = new Doc4Sort(entry.getKey(),entry.getValue());
        }
        List<String> orderedDocIds = new ArrayList<>();
        Arrays.sort(docs,Collections.reverseOrder());  // 内部改写了compareTo方法 未加@Override

        for(Doc4Sort doc:docs){
            orderedDocIds.add(doc.getDocId());
        }
        return orderedDocIds;
    }

    /**
     * 相关搜索排序逻辑，返回的list中不包含空值或重复词
     * @param docs
     * @param wordToFreqMap
     * @return
     */
    public List<String> wordSort(List<Doc> docs, Map<String, Integer> wordToFreqMap) {
        log.info("开始构建相关搜索: " + "分词数量: " + wordToFreqMap.size() + ", 提供文档数量: " + docs.size());

        Set<String> relatedSearch = new HashSet<>();
        List<String> wordsToFindRelatedSearch = new ArrayList<>(wordToFreqMap.keySet());

        // 若分词个数为1 则取一个关键词（即该分词本身）
        long docTotalNum = docLenStorage.getDocTotalNum();
        if (wordToFreqMap.size() == 1) {
            String keyWord = wordsToFindRelatedSearch.get(0);

            // 取docs中文档进行解析  获取关键词和其后一个位置的词语 拼接称为相关搜索词语
            int maxNum = Math.min(MAX_DOC_NUM_TO_PARSE_RELATED_SEARCH, docs.size());
            List<Doc> docsToParse = docs.subList(0, maxNum);
            for (Doc doc : docsToParse) {
                // 对单个文档和要解析的单个分词进行解析
                String relatedWord = calRelatedSearch(doc.getCaption(), keyWord);
                if (!relatedWord.equals(keyWord) && !relatedWord.isBlank()) {
                    relatedSearch.add(relatedWord);
                }
                if (relatedSearch.size() > 8) {
                    break;
                }
            }
        } else {
            // 若分词个数大于1
            String keyWord_1;
            String keyWord_2;

            // 1. 分词数量为2：不用重新选择关键词
            if (wordsToFindRelatedSearch.size() == 2) {
                keyWord_1 = wordsToFindRelatedSearch.get(0);
                keyWord_2 = wordsToFindRelatedSearch.get(1);
            } else {
                // 2. 分词数量大于2：计算所有分词idf权重，选择最大的两个作为关键词
                HashMap<String, BigDecimal> wordToIDFMap = new HashMap<>();

                // 计算idf权重
                for (String word : wordsToFindRelatedSearch) {
                    BigDecimal wordIDF = calWordIDF(word, docTotalNum);
                    wordToIDFMap.put(word, wordIDF);
                }

                // 选择idf权重最大的两个分词
                // todo 后面可以写个算法实现一下
                List<Map.Entry<String, BigDecimal>> wordToIDFList = new ArrayList<>(wordToIDFMap.entrySet());
                wordToIDFList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                keyWord_1 = wordToIDFList.get(0).getKey();
                keyWord_2 = wordToIDFList.get(1).getKey();
            }

            // 取docs中文档进行解析，获取关键词和其后一个位置的词语，拼接称为相关搜索词语
            int maxNum = Math.min(MAX_DOC_NUM_TO_PARSE_RELATED_SEARCH, docs.size());
            List<Doc> docsToParse = docs.subList(0, maxNum);
            for (Doc doc : docsToParse) {
                // 对单个文档和要解析的单个分词进行解析
                String relatedWord = calRelatedSearch(doc.getCaption(), keyWord_1, keyWord_2);
                if (!relatedWord.equals(keyWord_1) && !relatedWord.equals(keyWord_2)) {
                    relatedSearch.add(relatedWord);
                }
                if (relatedSearch.size() > 8) {
                    break;
                }
            }
        }

        log.info("构建相关搜索完成! " + "相关搜索: " + relatedSearch);

        return new ArrayList<>(relatedSearch);
    }

    /**
     *  计算单个分词的IDF  （未考虑单词与query关联度）
     */
    public BigDecimal calWordIDF(String word, long docTotalNum) {
        // 包含分词的文档数目
        IndexPartial indexPartial = indexPartialStorage.getIndexPartial(word);
        if (indexPartial == null) {
            return new BigDecimal(0);
        }
        long wordDocNum = indexPartial.getTempDataList().size();
        BigDecimal wordIDF = BigDecimal.valueOf(Math.log((docTotalNum - wordDocNum + 0.5) / (wordDocNum + 0.5)));
        return wordIDF;
    }

    /**
     * 对单个分词计算相关搜索
     * @param caption
     * @param keyWord
     * @return
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
