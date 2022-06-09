package team.snof.simplesearch.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.RedisUtils;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.adaptor.SearchAdaptor;
import team.snof.simplesearch.search.engine.Engine;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.doc.DocInfo;
import team.snof.simplesearch.search.model.dao.engine.ComplexEngineResult;
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

    @Autowired
    Engine engine;

    @Autowired
    WordSegmentation wordSegmentation;

    @Autowired
    RedisUtils redisUtils;

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

        // 获取过滤词信息
        List<Long> filterDocIds = findDocIdsByWord(filterWordList);
        
        if (filterDocIds.isEmpty()) {
            String queryToDocIdsCacheKey = "search:page:" + query + ":list";
            String queryToRelatedSearchKey = "search:related:" + query + ":list";
            searchEngineOrCacheForDocIds(queryToDocIdsCacheKey, queryToRelatedSearchKey, wordToFreqMap);
        }

    }

    // 这里因为引擎层的
    private List<Long> searchEngineOrCacheForDocIds(String queryToDocIdsCacheKey, String queryRelatedSearchKey, HashMap<String, Integer> wordToFreqMap) {
        // 这里先全部查出来，在业务侧做筛选。不然担心有一开始hasKey是true，get时缓存过期的情况。之后再优化。
        List<Object> docIdObjects = redisUtils.lGet(queryToDocIdsCacheKey, 0, -1);

        // 如果缓存里有
        if (!docIdObjects.isEmpty()) {
            return docIdObjects.stream().map(doc -> (Long) doc).collect(Collectors.toList());
        } else { // 如果redis里不存在或者过期了
            engine.find(wordToFreqMap)
        }


    }

//    private boolean hasValidCache(String key) {
//        return redisUtils.hasKey(key);
//    }

    private SearchResponseVO searchCache(boolean doFilter) {
        // 查询缓存

        request.setTotal(queryCache.size());

        // Redis缓存：key=query，value=doc_id_list
        if (redisTemplate.hasKey(queryCacheKey)) {
            long queryCacheSize = queryCache.size();

            // Redis的rlange左右都是闭区间
            long start = (long) (pageNum - 1) * pageSize;
            if (start > queryCache.size() - 1) {
                throw new IllegalArgumentException("所查询的记录超出范围!");
            }
            long end = Math.min((long) pageNum * pageSize - 1, queryCacheSize - 1);

            List<String> relatedSearches = (List<String>) queryRelatedSearchCache.get(queryRelatedSearchKey);

            List<Long> docIds;
            if (filterDocIds.isEmpty()) {
                docIds = queryCache.range(start, end);
            } else  {
                docIds = queryCache.range(0, -1);
                docIds = filterDocIdsByIds(docIds, filterDocIds);
                docIds = docIds.subList((int) start, (int) end);
            }

            // 从引擎层获取doc
            List<Doc> docList = engine.batchFindDocs(docIds);

            return convertAndBuildResponse(docList, relatedSearches, request);
        }
    }

    private SearchResponseVO searchDB() {
        // 查询引擎
        int limit = (pageNum - 1) * pageSize;
        ComplexEngineResult engineResult = engine.rangeFind(segmentedWordMap, limit, pageSize);

        // 更新缓存
        for (Long totalDocId : engineResult.getTotalDocIds()) {
            queryCache.rightPush(totalDocId);
        }
        queryRelatedSearchCache.put(queryRelatedSearchKey, engineResult.getRelatedSearch());
        queryCache.rightPush()

        List<Doc> docList;
        // 如为第一页不用重新去搜索引擎查找
        if (pageNum == 1) {
            docList = engineResult.getDocs();
            // 过滤
            if (!filterDocIds.isEmpty()) {
                docList = filterDocsByIds(docList, filterDocIds);
            }
        } else {
            List<Long> docIds = engineResult.getTotalDocIds();

            // 过滤
            if (!filterDocIds.isEmpty()) {
                docIds = filterDocIdsByIds(docIds, filterDocIds);
            }

            // 如不为第一页需回表
            docList = engine.batchFindDocs(docIds);
        }

        return convertAndBuildResponse(docList, engineResult.getRelatedSearch(), request);
    }

    private SearchResponseVO convertAndBuildResponse(List<Doc> docList, List<String> relatedSearchList, SearchRequestVO request) {
        // 准备DocVO
        List<DocVO> docVOList = SearchAdaptor.convertDocListToDocVOList(docList);

        request.setTotal((long) docList.size());

        return SearchResponseVO.builder()
                .docVOList(docVOList)
                .relatedSearchList(relatedSearchList)
                .query(request)
                .build();
    }

    private List<Long> findDocIdsByWord(List<String> words) {
        if (words.size() == 0) {
            return Collections.emptyList();
        }

        // 获取过滤词对应索引
        List<Index> indices = engine.batchFindIndexes(words);
        // 从索引获取所有要过滤的docids
        List<Long> filterDocIds = new ArrayList<>();
        for (Index index : indices) {
            List<DocInfo> docIdAndCorrList = index.getDocInfoList();
            List<Long> docIds = docIdAndCorrList.stream().map(DocInfo::getDocId).collect(Collectors.toList());
            filterDocIds.addAll(docIds);
        }

        return filterDocIds;
    }

    private List<Long> filterDocIdsByIds(List<Long> docIds, List<Long> idsToFilter) {
        if (idsToFilter.isEmpty()) {
            return docIds;
        }

        return docIds.stream()
                .filter(id -> !idsToFilter.contains(id))
                .collect(Collectors.toList());
    }

    private List<Doc> filterDocsByIds(List<Doc> docs, List<Long> idsToFilter) {
        if (idsToFilter.isEmpty()) {
            return docs;
        }

        return docs.stream()
                .filter(doc -> !idsToFilter.contains(doc.getSnowflakeDocId()))
                .collect(Collectors.toList());
    }

}
