package team.snof.simplesearch.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.model.vo.SearchResponseVO;
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

    public SearchResponseVO search(SearchRequestVO request) {

        // 查缓存是否有分页结果
        // 接口以参考这个 https://blog.csdn.net/AlbenXie/article/details/109348114
        // list批量添加
        ArrayList<String> list = new ArrayList<>();
        redisTemplate.boundListOps("listKey").rightPushAll(list);
        // list范围查询
        List listKey1 = redisTemplate.boundListOps("listKey").range(0, 10);
        // 获取List缓存的长度
        Long size = redisTemplate.boundListOps("listKey").size();


        // 分词

        // 简单过滤

        // 查缓存是否有分词的倒排索引

        // 调搜索引擎

        // 调排序

        return new SearchResponseVO();
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
