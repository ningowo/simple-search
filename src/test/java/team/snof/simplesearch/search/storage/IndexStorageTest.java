package team.snof.simplesearch.search.storage;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.snof.simplesearch.search.model.dao.doc.DocInfo;
import team.snof.simplesearch.search.model.dao.index.Index;

import java.math.BigDecimal;
import java.util.ArrayList;

@SpringBootTest
@Slf4j
public class IndexStorageTest {
    @Autowired
    IndexStorage indexStorage;

    @Test
    public void findTest() {
        ArrayList<Index> indices = new ArrayList<>();
        ArrayList<DocInfo> docInfos = new ArrayList<>();
        docInfos.add(new DocInfo(4L, new BigDecimal("0.1")));
        docInfos.add(new DocInfo(5L, new BigDecimal("0.2")));
        docInfos.add(new DocInfo(6L, new BigDecimal("0.3")));
        indices.add(new Index("test1", docInfos));
        indices.add(new Index("test2", docInfos));
        indices.add(new Index("test3", docInfos));
        indexStorage.saveBatch(indices);
    }
}
