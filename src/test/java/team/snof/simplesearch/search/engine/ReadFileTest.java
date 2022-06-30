package team.snof.simplesearch.search.engine;

import org.junit.jupiter.api.Test;
import team.snof.simplesearch.common.util.CSVFileReader;
import team.snof.simplesearch.search.model.dao.Doc;

import java.util.List;

public class ReadFileTest {
    @Test
    public void read() {
        String filePath = "C:\\Users\\Devour\\Desktop\\data\\wukong_100m_8.csv";
        List<Doc> docs = CSVFileReader.readFile(filePath);
        System.out.println(docs.get(docs.size() - 1));
    }
}
