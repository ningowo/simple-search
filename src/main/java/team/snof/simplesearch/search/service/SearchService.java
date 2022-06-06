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
import team.snof.simplesearch.search.model.dao.doc.Doc;
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
        List<String> filterWordList = request.getFilterWordList();
        String query = request.getQuery();
        Integer pageNum = request.getPageNum();
        Integer pageSize = request.getPageSize();

        // 获取过滤词信息
        List<Long> filterDocIds = getFilterDocIds(filterWordList);

        // 查询缓存
        redisTemplate.setValueSerializer(RedisSerializer.json());
        String queryCacheKey = "search:page:" + query + ":list";
        String queryRelatedSearchKey = "search:related:" + query + ":list";;

        BoundListOperations queryCache = redisTemplate.boundListOps(queryCacheKey);
        BoundHashOperations queryRelatedSearchCache = redisTemplate.boundHashOps(queryRelatedSearchKey);

        // Redis缓存：key=query，value=doc_id_list
        if (redisTemplate.hasKey(queryCacheKey)) {
            List<Long> docIds;
            if (pageNum == null || pageSize == null) {
                pageNum = 1;
                pageSize = DEFAULT_PAGE_SIZE;
            }

            // Redis的rlange左右都是闭区间
            long start = (pageNum - 1) * pageSize;
            long end = pageNum * pageSize - 1;

            if (start < queryCache.size() - 1) {
                throw new IllegalArgumentException("所查询的记录超出范围!");
            }
            end = Math.min(end, queryCache.size());

            // 从缓存查询
            docIds = queryCache.range(start, end);
            List<String> relatedSearches = (List<String>) queryRelatedSearchCache.get(queryRelatedSearchKey);

            // 过滤
            if (!filterDocIds.isEmpty()) {
                docIds = filterDocIdsByIds(docIds, filterDocIds);
            }

            // 从引擎层获取doc
            List<Doc> docList = engine.batchFindDocs(docIds);

            // 准备DocVO
            List<DocVO> docVOList = SearchAdaptor.convertDocListToDocVOList(docList);

            return SearchResponseVO.builder()
                    .docVOList(docVOList)
                    .relatedSearchList(relatedSearches)
                    .build();
        }

        // 如缓存中没有该query
        // 分词
        Map<String, Integer> segmentedWordMap = wordSegmentation.segment(query, filterWordList);

        // 查询引擎
        ComplexEngineResult engineResult = engine.rangeFind(segmentedWordMap, pageNum, pageSize);

        // 更新缓存
        queryCache.rightPush(engineResult.getTotalDocIds());

        List<Doc> docList;
        // 如为第一页不用重新去搜索引擎查找
        if (pageNum == 1) {
            docList = engineResult.getDocs();
            // 过滤
            if (!filterDocIds.isEmpty()) {
                filterDocsByIds(docList, filterDocIds);
            }
        } else {
            List<Long> docIds = engineResult.getTotalDocIds();

            // 过滤
            if (!filterDocIds.isEmpty()) {
                filterDocIdsByIds(docIds, filterDocIds);
            }

            // 如不为第一页需回表
            docList = engine.batchFindDocs(docIds);
        }

        // 准备DocVO
        List<DocVO> docVOList = SearchAdaptor.convertDocListToDocVOList(docList);

        return SearchResponseVO.builder()
                .docVOList(docVOList)
                .relatedSearchList(engineResult.getRelatedSearch())
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
            List<Pair<Long, BigDecimal>> docIdAndCorrList = index.getDocIdAndCorrList();
            List<Long> docIds = docIdAndCorrList.stream().map(Pair::getLeft).collect(Collectors.toList());
            filterDocIds.addAll(docIds);
        }

        return filterDocIds;
    }

    private List<Long> filterDocIdsByIds(List<Long> docIds, List<Long> idsToFilter) {
        return docIds.stream().filter(id -> idsToFilter.contains(id)).collect(Collectors.toList());
    }

    private List<Doc> filterDocsByIds(List<Doc> docs, List<Long> idsToFilter) {
        return docs.stream()
                .filter(doc -> idsToFilter.contains(doc.getSnowflakeDocId()))
                .collect(Collectors.toList());
    }


    public String test() {
        // 测试redis
        redisTemplate.setValueSerializer(RedisSerializer.json());
        String key = "key1";
        List<String> value = new ArrayList<>();
        value.add("1");
        value.add("2");
        value.add("3");
        Doc doc = new Doc();
        doc.setCaption("qhwkjehqwkj");
        doc.setUrl("www.baidu.com");

        redisTemplate.opsForValue().set("doc", doc);
        System.out.println(redisTemplate.opsForValue().get("doc"));

        Long size = redisTemplate.opsForList().size(key);

        return "Result: " + size;
    }

}
