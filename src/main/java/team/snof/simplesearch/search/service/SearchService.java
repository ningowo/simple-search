package team.snof.simplesearch.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.infra.redis.Holder;
import team.snof.simplesearch.search.infra.redis.RedisPoolExecutor;
import team.snof.simplesearch.search.model.vo.SearchListResponseVO;
import team.snof.simplesearch.search.model.vo.SearchRequestVO;

import java.util.List;

@Component
public class SearchService {

    @Autowired
    WordSegmentation wordSegmentation;

    @Autowired
    RedisPoolExecutor redisPool;

    public SearchListResponseVO search(SearchRequestVO request) {

        // 查缓存是否有分页结果
        // 举个例子
        findDocsInJedis("key", 0, 10);

        // 分词

        // 简单过滤

        // 查缓存是否有分词的倒排索引

        // 调搜索引擎

        // 调排序

        return new SearchListResponseVO();
    }

    private List<String> findDocsInJedis(String key, long start, long stop) {

        Holder<List<String>> holder = new Holder<>();
        redisPool.execute(redis -> {
            // 具体看api https://www.runoob.com/redis/redis-lists.html
            List<String> list = redis.lrange(key, start, stop);
            holder.value(list);
        });

        return holder.value();
    }
}
