package team.snof.simplesearch;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.snof.simplesearch.search.component.DocPaser;
import team.snof.simplesearch.search.mapper.DocMapper;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.service.DocService;

import java.util.List;

@SpringBootTest
class SimpleSearchApplicationTests {
    @Autowired
    DocMapper docMapper;
    @Autowired
    DocService docService;
    @Autowired
    DocPaser docPaser;

    @Test
    public void mapperTest() {
        List<Doc> docs = docMapper.selectList(new QueryWrapper<>());
        for (Doc doc : docs) {
            System.out.println(doc);
        }
    }

    @Test
    public void importCSVDataTest() {
        docPaser.parse();
    }
}
