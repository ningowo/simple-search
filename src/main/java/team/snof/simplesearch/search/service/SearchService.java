package team.snof.simplesearch.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.infra.JedisPoolSingleton;
import team.snof.simplesearch.search.model.vo.SearchListResponseVO;
import team.snof.simplesearch.search.model.vo.SearchRequestVO;

import java.util.List;

@Component
public class SearchService {

    @Autowired
    WordSegmentation wordSegmentation;

    JedisPool jedisPool = JedisPoolSingleton.getJedisPool();

    public SearchListResponseVO search(SearchRequestVO request) {

        // 查缓存是否有分页结果
        // 举个例子
        findDocInJedis();

        // 分词

        // 简单过滤

        // 查缓存是否有分词的倒排索引

        // 调搜索引擎

        // 调排序

        return new SearchListResponseVO();
    }

    private List findDocInJedis() {
        List<String> list;
        // 缓存使用记得用try-with-resource！ 用完自动 close
        try (Jedis jedis = jedisPool.getResource()) {
            // 具体看api https://www.runoob.com/redis/redis-lists.html
            jedis.llen("query");
            list = jedis.lrange("key", 0, 10);
        }

        return list;
    }
}
