package team.snof.simplesearch;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import team.snof.simplesearch.SimpleSearchApplication;
import team.snof.simplesearch.search.mapper.DocLenMapper;
import team.snof.simplesearch.search.model.dao.DocLen;




@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = SimpleSearchApplication.class)
public class MapperTests {

    @Autowired
    private DocLenMapper docLenMapper;


    @Test
    public void testInsertUser() {
        DocLen docLen = new DocLen();
        docLen.setDocId(5);
        docLen.setDocLen(20);
        int rows = docLenMapper.insertDocLen(docLen);
        System.out.println(rows);
    }



}

