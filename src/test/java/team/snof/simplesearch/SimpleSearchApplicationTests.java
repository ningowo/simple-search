package team.snof.simplesearch;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.snof.simplesearch.search.component.DocPaser;
import team.snof.simplesearch.search.mapper.DocMapper;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.service.DocService;

import java.util.List;

@SpringBootTest
@Slf4j
class SimpleSearchApplicationTests {
    @Autowired
    DocMapper docMapper;
    @Autowired
    DocService docService;
    @Autowired
    DocPaser docPaser;

    @Test
    void contextLoads() {
        log.info("添加注解之后直接在方法里用就行");
    }

    @Test
    public void importCSVDataTest() {
        docPaser.parse();
    }
}
