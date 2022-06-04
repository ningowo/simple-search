package team.snof.simplesearch;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.snof.simplesearch.search.engine.storage.MetaDataStorage;
import team.snof.simplesearch.search.model.dao.meta.DocMetaData;

import java.math.BigDecimal;

@SpringBootTest
@Slf4j
public class DocMetaDataTest {
    @Autowired
    MetaDataStorage metaDataStorage;

    @Test
    void saveTest() {
        DocMetaData docMetaData = new DocMetaData();
        docMetaData.setDocNum(0L);
        docMetaData.setDocLen(0L);
        docMetaData.setAvgLen(0L);
        metaDataStorage.save(docMetaData);
    }

    @Test
    void updateTest() {
        DocMetaData docMetaData = new DocMetaData();
        docMetaData.setDocNum(3L);
        docMetaData.setDocLen(10L);
        Long update = metaDataStorage.update(docMetaData);
        System.out.println(update);
    }
}
