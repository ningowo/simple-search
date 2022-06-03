package team.snof.simplesearch.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.engine.Engine;
import team.snof.simplesearch.search.model.vo.SearchListResponseVO;
import team.snof.simplesearch.search.model.vo.SearchRequestVO;

import java.util.ArrayList;
import java.util.List;


@Component
public class SearchService {

    @Autowired
    WordSegmentation wordSegmentation;

    // spring容器没启动的时候会爆红，没办法。直接用就行
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    SearchService searchService;

    @Autowired
    Engine engine;

    public SearchListResponseVO search(SearchRequestVO request) {

        // 查query结果
        // Redis接口以参考这个 https://blog.csdn.net/AlbenXie/article/details/109348114
        // 自己考虑具体缓存什么内容
        redisTemplate.opsForHash().get();
//        if () {
//            // 返回结果
//        }

        // 分词
        List<String> words = wordSegmentation.segment(request.getQuery());

        // 简单过滤
        words.removeAll(request.getFilterWordList());

        // 查缓存是否有分词的倒排索引
        for (String word: words) {
            // redisTemplate
        }

        // 调搜索引擎
//        if () { // 缓存里分词的索引不够
//            engine.getIndex();
//        }

        // 缓存分词索引结果

        // 调排序
        //wyh: order(query分词列表，Index列表)
        //List<Index> orderedIndex= order(words,List<Index>)

        // 根据索引的docid查出doc，也就是query的结果
        // engine.batchGetDoc()

        // 缓存query结果

        return new SearchListResponseVO("结果对象");
    }

    public String test() {

        String key = "key1";
        List<String> value = new ArrayList<>();
        value.add("1");
        value.add("2");
        value.add("3");
        redisTemplate.opsForList().rightPushAll(key, value);

        Long size = redisTemplate.opsForList().size(key);

        return "Result: " + size;
    }

}
