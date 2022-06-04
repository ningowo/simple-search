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
}
