package team.snof.simplesearch.search.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.RedisUtils;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.adaptor.SearchAdaptor;
import team.snof.simplesearch.search.engine.Engine;
import team.snof.simplesearch.search.model.dao.InvertedIndex;
import team.snof.simplesearch.search.model.dao.Doc;
import team.snof.simplesearch.search.model.vo.DocVO;
import team.snof.simplesearch.search.model.vo.SearchRequestVO;
import team.snof.simplesearch.search.model.vo.SearchResponseVO;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SearchService {

    private static Integer DEFAULT_PAGE_SIZE = 30;

    // 默认缓存过期时间，单位：秒
    private static final Integer DEFAULT_CACHE_EXPIRE_TIME = 60 * 10;

    // 最多进行相关搜索检索的文档
    private static final int MAX_DOC_NUM_TO_PARSE_RELATED_SEARCH = 10;

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
        log.info("开始分词：");
        HashMap<String, Integer> wordToFreqMap = wordSegmentation.segment(query, filterWordList);
        // 上一句调用分词器的时候  已经把过滤词过滤掉了 下面过滤步骤可省略
        // 如果query中有过滤词，在这一步直接过滤掉
        for (String word : wordToFreqMap.keySet()) {
            if (filterWordList.contains(word)) {
                wordToFreqMap.remove(word);
            }
        }
        log.info("分词完成：{}", wordToFreqMap.keySet());

        // 对分词获取文档，并排序
        List<String> sortedDocIds = engine.findSortedDocIds(wordToFreqMap);
        log.info("docId获取并排序完成：" + sortedDocIds.size());

        // 过滤
        sortedDocIds = filterDocIdsByIds(sortedDocIds, filterWordList);
        int totalDocNum = sortedDocIds.size();
        if (totalDocNum == 0) {
            return SearchAdaptor.getEmptyResponseVO(request);
        }
        log.info("docId过滤完成：" + sortedDocIds.size());

        // 获取相关搜索
        // 还是需要传入文档
        List<String> relatedSearchDocIds = new ArrayList<>();
        int relatedSearchDocNum = Math.min(totalDocNum, MAX_DOC_NUM_TO_PARSE_RELATED_SEARCH);
        relatedSearchDocIds = sortedDocIds.subList(0, relatedSearchDocNum);
        List<String> relatedSearch = engine.findRelatedSearch(relatedSearchDocIds ,wordToFreqMap);

        // 设置总文档数为过滤完毕的文档数
        request.setTotal((long) totalDocNum);

        // 分页
        int start = (pageNum - 1) * pageSize;
        if (start >= totalDocNum) {
            throw new IllegalArgumentException("所查询的记录超出范围!");
        }
        int end = (int) Math.min((long) pageNum * pageSize, totalDocNum);
        List<String> pageDocIds = sortedDocIds.subList(start, end);
        log.info("分页完成：" + pageDocIds);

        // 查询文档
        List<Doc> docs = engine.batchFindDocs(pageDocIds);
        log.info("文档查询完成：" + docs);

        return convertAndBuildResponse(docs, relatedSearch, request);
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

    private List<String> filterDocIdsByIds(List<String> docIds, List<String> filterWordList) {
        List<InvertedIndex> invertedIndexList = engine.findInvertedIndexList(filterWordList);

        Set<String> idsToFilter = invertedIndexList.stream()
                .map(InvertedIndex::getDocIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        if (idsToFilter.isEmpty()) {
            return docIds;
        }

        return docIds.stream()
                .filter(id -> !idsToFilter.contains(id))
                .collect(Collectors.toList());
    }

}
