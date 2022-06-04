package team.snof.simplesearch.search.service;

import io.swagger.models.auth.In;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.repository.query.ParameterOutOfBoundsException;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.IKAnalyzerUtil;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.engine.Engine;
import team.snof.simplesearch.search.model.bo.CompleteResult;
import team.snof.simplesearch.search.model.bo.CompleteResultWithRange;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.vo.DocVO;
import team.snof.simplesearch.search.model.vo.SearchRequestVO;
import team.snof.simplesearch.search.model.vo.SearchResponseVO;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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
    IKAnalyzerUtil ikAnalyzerUtil;

    // spring容器没启动的时候会爆红，没办法。直接用就行
    @Autowired
    private RedisTemplate redisTemplate;

    public SearchResponseVO search(SearchRequestVO request) throws IOException {
        List<String> filterWordList = request.getFilterWordList();
        String query = request.getQuery();
        Integer pageNum = request.getPageNum();
        Integer pageSize = request.getPageSize();

        // 获取过滤词信息
        List<Long> filterDocIds = new ArrayList<>();
        if (!filterWordList.isEmpty()) {
            // 获取过滤词对应索引
            List<Index> indices = engine.batchFindIndexes(filterWordList);
            // 从索引获取所有要过滤的docids
            for (Index index : indices) {
                List<Pair<Long, BigDecimal>> docIdAndCorrList = index.getDocIdAndCorrList();
                List<Long> docIds = docIdAndCorrList.stream().map(Pair::getLeft).collect(Collectors.toList());
                filterDocIds.addAll(docIds);
            }
        }

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
                docIds = docIds.stream().filter(id -> filterDocIds.contains(id)).collect(Collectors.toList());
            }

            // 从引擎层获取doc
            List<Doc> docList = engine.batchFindDocs(docIds);

            // 准备DocVO
            List<DocVO> docVOList = docList.stream().map(DocVO::buildDocVO).collect(Collectors.toList());

            return SearchResponseVO.builder()
                    .docVOList(docVOList)
                    .relatedSearchList(relatedSearches)
                    .build();
        }

        // 如缓存中没有该query
        // 分词
        Map<String, Integer> segmentedWordMap = ikAnalyzerUtil.analyze(query, filterWordList);

        // 查询引擎
        CompleteResultWithRange engineResult = engine.rangeFind(segmentedWordMap, pageNum, pageSize);

        // 更新缓存
        queryCache.rightPush(engineResult.getTotalDocIds());

        // 如为第一页不用重新去搜索引擎查找
        if (pageNum == 1) {
            List<Doc> firstPageDoc = engineResult.getDocs();
            // 过滤
            if (!filterDocIds.isEmpty()) {
                firstPageDoc = firstPageDoc.stream()
                        .filter(doc -> filterDocIds.contains(doc.getSnowflakeDocId()))
                        .collect(Collectors.toList());
            }

            // 准备DocVO
            List<DocVO> docVOList = firstPageDoc.stream().map(DocVO::buildDocVO).collect(Collectors.toList());

            return SearchResponseVO.builder()
                    .docVOList(docVOList)
                    .relatedSearchList(engineResult.getRelatedSearch())
                    .build();
        }
        
        
        // 如不为第一页需回表
        List<Long> docIds = engineResult.getTotalDocIds();
        
        // 过滤
        if (!filterDocIds.isEmpty()) {
            docIds = docIds.stream().filter(id -> filterDocIds.contains(id)).collect(Collectors.toList());
        }

        List<Doc> docList = engine.batchFindDocs(docIds);

        // 准备DocVO
        List<DocVO> docVOList = docList.stream().map(DocVO::buildDocVO).collect(Collectors.toList());

        return SearchResponseVO.builder()
                .docVOList(docVOList)
                .relatedSearchList(engineResult.getRelatedSearch())
                .build();
    }

    // 在src/test下开个test的类，不要写在实际的类里
    public String test() {
        redisTemplate.setValueSerializer(RedisSerializer.json());
        String key = "key1";
        List<String> value = new ArrayList<>();
        value.add("1");
        value.add("2");
        value.add("3");
        Doc doc = new Doc();
        doc.setCaption("qhwkjehqwkj");
        doc.setUrl("www.baidu.com");

//        redisTemplate.opsForList().rightPushAll(key, value);
        redisTemplate.opsForValue().set("doc", doc);
        System.out.println(redisTemplate.opsForValue().get("doc"));

        Long size = redisTemplate.opsForList().size(key);

        return "Result: " + size;
    }

}
