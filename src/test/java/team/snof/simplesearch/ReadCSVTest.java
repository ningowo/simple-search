package team.snof.simplesearch;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.snof.simplesearch.common.util.CSVFileReader;
import team.snof.simplesearch.search.engine.storage.DocStorage;
import team.snof.simplesearch.search.model.dao.doc.Doc;

import java.util.HashSet;
import java.util.List;

@SpringBootTest
@Slf4j
public class ReadCSVTest {
    @Autowired
    DocStorage docStorage;

    @Test
    public void readCsvTest() {
        CSVFileReader.init(1000, 50000);
        List<Doc> docs = CSVFileReader.read();
        HashSet<Doc> set = new HashSet<>(docs);
        System.out.println(set.size());
    }
}
