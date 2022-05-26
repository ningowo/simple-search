package team.snof.simplesearch.utils;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import team.snof.simplesearch.search.model.dao.doc.Doc;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 文档导入工具
 */
public class CSVFileReader {
    private static final String workDir = System.getProperty("user.dir");
    private static final String docPath = workDir + "\\data.csv";
    private static final Integer OFFSET_DATA = 2;   // 从第二行开始读
    public static final Integer READ_NUM = 5;   // 每次读的条数
    public static final Integer READ_SUM = 50; // 共需要读取的总条数
    private static AtomicInteger count = new AtomicInteger(-1);

    public static List<Doc> read() {
        int start = count.incrementAndGet();
        List<Doc> indices = new ArrayList<>();
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(docPath), "GBK");
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
            CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(csvParser).withSkipLines(OFFSET_DATA + start * READ_NUM).build();
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                String url = values[0];
                String caption = values[1];
                indices.add(new Doc(url, caption));
                if (indices.size() == READ_NUM)
                    break;
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return indices;
    }
}
