package team.snof.simplesearch.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.RedisUtils;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.adaptor.SearchAdaptor;
import team.snof.simplesearch.search.engine.Engine;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.doc.DocInfo;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.vo.DocVO;
import team.snof.simplesearch.search.model.vo.SearchRequestVO;
import team.snof.simplesearch.search.model.vo.SearchResponseVO;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SearchService {

    private static Integer DEFAULT_PAGE_SIZE = 30;

    // 默认缓存过期时间，单位：秒
    private static final Integer DEFAULT_CACHE_EXPIRE_TIME = 60 * 10;

    // 计算相关搜索的最大文档解析量
    public static final Integer MAX_DOC_NUM_FOR_RELATED_SEARCH = 20;

    @Autowired
    Engine engine;

    @Autowired
    WordSegmentation wordSegmentation;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public SearchResponseVO search(SearchRequestVO request) throws IOException {
        // 获取请求参数
        String query = request.getQuery();
        List<String> filterWordList = request.getFilterWordList();
        Integer pageNum = request.getPageNum();
        Integer pageSize = request.getPageSize();

        // 已在分页实体类限制页码和每页数量大于0，这里只处理null值
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        // 分词
        HashMap<String, Integer> wordToFreqMap = wordSegmentation.segment(query, filterWordList);
        // 如果query中有过滤词，在这一步直接过滤掉
        for (String word : wordToFreqMap.keySet()) {
            if (filterWordList.contains(word)) {
                wordToFreqMap.remove(word);
            }
        }

        // TODO 如果能找个办法把query和过滤词合并成一个搜索条件，那么以此建立缓存key，就能比较简单的缓存结果。现在因为相关搜索需要获取doc,
        //  过滤之后还要获取doc，这两个重复度有点高
        //  （这里缓存是为了分页使用的，因为有相同条件重复查询）

        // 获取排好序的文档id
        String queryToDocIdsCacheKey = "search:page:" + query + ":list";
        List<Long> sortedAllDocIds = searchEngineOrCacheForDocIds(queryToDocIdsCacheKey, wordToFreqMap);
        if (sortedAllDocIds.isEmpty()) {
            return getEmptyResponseVO(request);
        }

        Map<Long, Doc> docMap = new HashMap<>();
        // 1. 尝试获取相关搜索
        String queryToRelatedSearchKey = "search:related:" + query + ":list";
        List<String> relatedSearch = redisUtils.lGetAll(queryToRelatedSearchKey);

        // 2. 如果redis里不存在或者过期了，查询全部doc计算相关搜索，并更新缓存
        if (relatedSearch.isEmpty()) {
            // 查询doc
            int end = Math.min(MAX_DOC_NUM_FOR_RELATED_SEARCH, sortedAllDocIds.size());
            List<Long> lessDocIdsForRelatedSearch = sortedAllDocIds.subList(0, end);
            List<Doc> lessDocs = engine.batchFindDocs(lessDocIdsForRelatedSearch);
            lessDocs.forEach(doc -> docMap.put(doc.getSnowflakeDocId(), doc));

            // 更新缓存（只根据query生成的相关搜索）
            relatedSearch = engine.findRelatedSearch(lessDocs, wordToFreqMap);
            redisUtils.lSetAll(queryToRelatedSearchKey, relatedSearch, DEFAULT_CACHE_EXPIRE_TIME);
        }

        // 过滤
        Set<Long> filterDocIds = findDocIdsByWord(filterWordList);
        List<Long> filteredAndSortedDocIds = filterDocIdsByIds(sortedAllDocIds, filterDocIds);
        int totalDocNum = filteredAndSortedDocIds.size();
        if (totalDocNum == 0) {
            return getEmptyResponseVO(request);
        }

        // 设置总文档数为过滤完毕的文档数
        request.setTotal((long) totalDocNum);

        // 分页
        int start = (pageNum - 1) * pageSize;
        if (start >= totalDocNum) {
            throw new IllegalArgumentException("所查询的记录超出范围!");
        }
        int end = Math.min(pageNum * pageSize, totalDocNum);
        List<Long> pageDocIds = filteredAndSortedDocIds.subList(start, end);

        // 查询文档
        List<Doc> resDocs = new ArrayList<>();
        for (Long docId : pageDocIds) {
            if (docMap.containsKey(docId)) {
                resDocs.add(docMap.get(docId));
            } else {
                resDocs.add(engine.findDoc(docId));
            }
        }

        return convertAndBuildResponse(resDocs, relatedSearch, request);
    }

    private List<Long> searchEngineOrCacheForDocIds(String queryToDocIdsCacheKey, HashMap<String, Integer> wordToFreqMap) {
        // 这里先全部查出来，在业务侧做筛选。不然担心有一开始hasKey是true，get时缓存过期的情况。之后再优化。
        List<Long> sortedDocIds = redisUtils.lGetAll(queryToDocIdsCacheKey);

        // 如果缓存里有
        if (!sortedDocIds.isEmpty()) {
            return sortedDocIds;
        } else {
            // 如果redis里不存在或者过期了，查询并更新缓存
            // 查询
            sortedDocIds = engine.findSortedDocIds(wordToFreqMap);

            // 如果引擎也查不到，返回空list
            if (sortedDocIds.isEmpty()) {
                return Collections.emptyList();
            }

            // 更新
            // 这里往缓存里放的搜索排好序的文档，和引擎层的缓存作用不同
            redisUtils.lSetAll(queryToDocIdsCacheKey, sortedDocIds, DEFAULT_CACHE_EXPIRE_TIME);

            return sortedDocIds;
        }
    }

    private SearchResponseVO convertAndBuildResponse(List<Doc> docList, List<String> relatedSearchList, SearchRequestVO request) {
        // 准备DocVO
        List<DocVO> docVOList = SearchAdaptor.convertDocListToDocVOList(docList);

        return SearchResponseVO.builder()
                .docVOList(docVOList)
                .relatedSearchList(relatedSearchList)
                .query(request)
                .build();
    }

    private Set<Long> findDocIdsByWord(List<String> words) {
        if (words.size() == 0) {
            return Collections.emptySet();
        }

        // 获取过滤词对应索引
        List<Index> indices = engine.batchFindIndexes(words);
        // 从索引获取所有要过滤的docids
        Set<Long> filterDocIds = new HashSet<>();
        for (Index index : indices) {
            List<DocInfo> docIdAndCorrList = index.getDocInfoList();
            for (DocInfo docInfo : docIdAndCorrList) {
                filterDocIds.add(docInfo.getDocId());
            }
        }

        return filterDocIds;
    }

    private List<Long> filterDocIdsByIds(List<Long> docIds, Set<Long> idsToFilter) {
        if (idsToFilter.isEmpty()) {
            return docIds;
        }

        return docIds.stream()
                .filter(id -> !idsToFilter.contains(id))
                .collect(Collectors.toList());
    }

    private List<Doc> filterDocsByIds(List<Doc> docs, Set<Long> idsToFilter) {
        if (idsToFilter.isEmpty()) {
            return docs;
        }

        return docs.stream()
                .filter(doc -> !idsToFilter.contains(doc.getSnowflakeDocId()))
                .collect(Collectors.toList());
    }

    private SearchResponseVO getEmptyResponseVO(SearchRequestVO request) {
        request.setTotal(0L);
        return SearchResponseVO.builder()
                .docVOList(Collections.emptyList())
                .relatedSearchList(Collections.emptyList())
                .query(request)
                .build();
    }

}
