package team.snof.simplesearch.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.IKAnalyzerUtil;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.engine.DocParser;
import team.snof.simplesearch.search.engine.Engine;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.vo.DocVO;
import team.snof.simplesearch.search.model.vo.SearchListResponseVO;
import team.snof.simplesearch.search.model.vo.SearchRequestVO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Component
public class SearchService {

    @Autowired
    Engine engine;

    @Autowired
    WordSegmentation wordSegmentation;

    @Autowired
    IKAnalyzerUtil ikAnalyzerUtil;

    // spring容器没启动的时候会爆红，没办法。直接用就行
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SortService sortService;


    public SearchListResponseVO search(SearchRequestVO request) throws IOException {
        //设置redis对象的序列化方式
        redisTemplate.setValueSerializer(RedisSerializer.json());
        SearchListResponseVO res = new SearchListResponseVO();

        List<String> filterWordList = request.getFilterWordList();
        String query = request.getQuery();
        Integer pageNum = request.getPageNum();
        Integer pageSize = request.getPageSize();

        // ------------一级缓存------------
        // 查缓存是否有分页结果
        // 接口以参考这个 https://blog.csdn.net/AlbenXie/article/details/109348114
        // list批量添加
        ArrayList<String> list = new ArrayList<>();
        BoundListOperations queryRes = redisTemplate.boundListOps(query);
        queryRes.rightPushAll(list);

        //zyg：每条query，固定只缓存排序后的前30条；考虑到如果之后查询会跳着查到如第61-70条，这些记录不能直接添加到缓存中

        //zyg：先查缓存，如果缓存中存在则直接返回结果
        //判断缓存中是否存在的两个依据：
        //  1.redis中是否存在该key
        //  2.redis中有该key，但是分页的查询参数已超过value的size
        if (redisTemplate.hasKey(query)) {
            if (queryRes.size() >= pageNum * pageSize) {
                res.setDocVOList(queryRes.range((pageNum - 1) * pageSize, pageNum * pageSize));
                return res;
            }
        }

        //zyg：如果缓存中没有该query，则去数据库里查，查到后并缓存
        // 分词 并 简单过滤
        Map<String, Integer> segmentedWordMap = ikAnalyzerUtil.analyze(query, filterWordList);

        // ------------二级缓存,key:分好的词, value:倒排索引类------------

        List<Index> indexList = new ArrayList<>();
        // 查缓存是否有分词的倒排索引
        for (String segmentedWord: segmentedWordMap.keySet()) {
            if (!redisTemplate.hasKey(segmentedWord)) {     //缓存里的分词索引不存在
////                Index index = engine.getIndex(segmentedWord);   //去数据库中查
//                indexList.add(index);
//                redisTemplate.boundValueOps(segmentedWord).set(index); //缓存分词索引结果
            } else {
                indexList.add((Index) redisTemplate.boundValueOps(segmentedWord).get());
            }
        }

        // 调排序
        //wyh: order(query分词列表，Index列表)
        //zyg：考虑在此处限制返回的数目，比如30
        List<Long> orderRes = sortService.order(new ArrayList<>(segmentedWordMap.keySet()), indexList);

        // 根据索引的docid查出doc，也就是query的结果
//        List<DocVO> docs = engine.batchGetDoc(orderRes);
//        res.setDocVOList(docs);

        // 缓存query结果    //查到的所有都缓存吗？考虑在排序完成返回结果时做一个数量的限制
//        redisTemplate.boundListOps(query).rightPush(docs);

        return res;
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
        doc.setSnowflakeDocId(31231231L);

//        redisTemplate.opsForList().rightPushAll(key, value);
        redisTemplate.opsForValue().set("doc", doc);
        System.out.println(redisTemplate.opsForValue().get("doc"));

        Long size = redisTemplate.opsForList().size(key);

        return "Result: " + size;
    }

}
