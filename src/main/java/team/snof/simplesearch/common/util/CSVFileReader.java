package team.snof.simplesearch.common.util;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import team.snof.simplesearch.search.model.dao.doc.Doc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CSVFileReader {
    private static final String workDir = System.getProperty("user.dir");
    private static final String docPath = workDir + "\\data.csv";
    private static final Integer OFFSET_DATA = 2;   // 从第二行开始读
    public static final Integer READ_NUM = 50000;    // 每次读的条数
    public static final Integer READ_SUM = 80000;   // 总共需要读的条数
    public static AtomicInteger count;

    public static void init() {
        count = new AtomicInteger(0);
    }

    public static List<Doc> readDocsFromCSV() {
        return readDocsFromCSV(docPath);
    }

    public static List<Doc> readDocsFromCSV(String filePath) {
        int start = count.getAndIncrement();
        List<Doc> indices = new ArrayList<>();
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath), "GBK");
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
            CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(csvParser).withSkipLines(OFFSET_DATA + start * READ_NUM).build();
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                String url = values[0];
                String caption = values[1];
                indices.add(new Doc(url, caption));
//                if(indices.size() == READ_NUM)
//                    break;
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return indices;
    }
}
