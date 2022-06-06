package team.snof.simplesearch.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.IKAnalyzerUtil;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.engine.Engine;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.vo.SearchRequestVO;
import team.snof.simplesearch.search.model.vo.SearchResponseVO;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SearchService {

    private static Integer DEFAULT_RES_SIZE = 30;

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
        SearchResponseVO res = new SearchResponseVO();

        //设置redis对象的序列化方式
        redisTemplate.setValueSerializer(RedisSerializer.json());

        //获取过滤词列表
        List<String> filterWordList = request.getFilterWordList();
        //获取待查询语句
        String query = request.getQuery();
        //分页参数
        Integer pageNum = request.getPageNum();
        Integer pageSize = request.getPageSize();

        // 接口以参考这个 https://blog.csdn.net/AlbenXie/article/details/109348114
        BoundListOperations queryRes = redisTemplate.boundListOps(query);

        //缓存中key存放query，value存放doc_id_list

        //zyg：先查缓存，如果缓存命中

        //判断缓存中是否存在的两个依据：
        //  1.redis中是否存在该key
        if (redisTemplate.hasKey(query)) {
            List<Long> docIdList;
            if (pageNum != null && pageSize != null) {
                //如果分页参数小于待查询list的长度，正常执行查询；否则返回list的最后pageSize条
                if (pageNum * pageSize < queryRes.size()) {
                    docIdList = queryRes.range((pageNum - 1) * pageSize, pageNum * pageSize);
                } else {
                    docIdList = queryRes.range(queryRes.size() - pageSize, queryRes.size());
                }
            } else {
                docIdList = queryRes.range(0, DEFAULT_RES_SIZE);
            }
            res.setDocVOList(engine.batchFindDocs(docIdList));
            return res;
        }

        //zyg：如果缓存中没有该query，调用引擎层的查询接口，查到后并缓存

        // 分词 并 简单过滤
        Map<String, Integer> segmentedWordMap = ikAnalyzerUtil.analyze(query, filterWordList);
        SearchResponseVO responseVO;
        if (pageNum != null && pageSize != null) {
            responseVO = engine.rangeFind(segmentedWordMap, pageNum, pageSize);
        } else  {
            responseVO = engine.find(segmentedWordMap);
        }
        //查到以后将doc_id存入缓存当中
        queryRes.rightPush(responseVO.getDocIds());
        return responseVO;

    }


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
