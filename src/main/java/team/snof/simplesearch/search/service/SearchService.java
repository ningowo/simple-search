package team.snof.simplesearch.search.service;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.adaptor.SearchAdaptor;
import team.snof.simplesearch.search.engine.Engine;
import team.snof.simplesearch.search.engine.EngineImpl;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.doc.DocInfo;
import team.snof.simplesearch.search.model.dao.engine.ComplexEngineResult;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.vo.DocVO;
import team.snof.simplesearch.search.model.vo.SearchRequestVO;
import team.snof.simplesearch.search.model.vo.SearchResponseVO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SearchService {

    private static Integer DEFAULT_PAGE_SIZE = 30;

    @Autowired
    Engine engine;

    @Autowired
    WordSegmentation wordSegmentation;

    @Autowired
    private RedisTemplate redisTemplate;

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

        // 获取过滤词信息
        List<Long> filterDocIds = getFilterDocIds(filterWordList);

        // 如缓存中没有该query
        // 分词
        Map<String, Integer> segmentedWordMap = wordSegmentation.segment(query, filterWordList);

        if (redisTemplate.hasKey(queryCacheKey) &&) {
        }

    }

    private boolean hasValidCache(boolean doFilter) {

    }

    private SearchResponseVO searchCache(boolean doFilter) {
        // 查询缓存
        redisTemplate.setKeySerializer(RedisSerializer.json());
        redisTemplate.setValueSerializer(RedisSerializer.json());
        String queryCacheKey = "search:page:" + query + ":list";
        String queryRelatedSearchKey = "search:related:" + query + ":list";;

        BoundListOperations queryCache = redisTemplate.boundListOps(queryCacheKey);
        BoundHashOperations queryRelatedSearchCache = redisTemplate.boundHashOps(queryRelatedSearchKey);

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

    private List<Long> getFilterDocIds(List<String> filterWordList) {
        if (filterWordList.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取过滤词对应索引
        List<Index> indices = engine.batchFindIndexes(filterWordList);
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
