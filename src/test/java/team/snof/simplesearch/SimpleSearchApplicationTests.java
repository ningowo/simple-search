package team.snof.simplesearch;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.snof.simplesearch.search.service.SearchService;

@SpringBootTest
@Slf4j
class SimpleSearchApplicationTests {


    @Autowired
    private SearchService searchService;

    @Test
    void contextLoads() {
        log.info("添加注解之后直接在方法里用就行");
    }

    @Test
    void testRedis() {
        searchService.test();
    }

}
