package team.snof.simplesearch.search.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SearchServiceTest {

//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    @Test
//    void search() {
//        String key = "key1";
//        List<String> value = new ArrayList<>();
//        value.add("1");
//        value.add("2");
//        value.add("3");
//        redisTemplate.opsForList().rightPushAll("key", value);
//
//        List list = redisTemplate.opsForList().range(key, 0, 1);
//        System.out.println(list);
//    }
}