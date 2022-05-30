package team.snof.simplesearch.common.util;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import team.snof.simplesearch.search.engine.DocParser;
import team.snof.simplesearch.search.model.dao.doc.Doc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CSVFileReader {
    private static final String workDir = System.getProperty("user.dir");
    private static String FILE_PATH = workDir + "\\data.csv";
    private static final Integer OFFSET_DATA = 2;   // 从第二行开始读
    public static Integer READ_NUM = 1000;    // 每次读的条数
    public static Integer READ_SUM = 50000;   // 总共需要读的条数

    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 30;
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000, false));

    public static AtomicInteger count;

    /**
     * 每读取一个文件之前，都需要先执行 init 方法初始化参数
     */
    public static void init() {
        count = new AtomicInteger(0);
    }

    public static void init(int readNum, int readSum) {
        READ_NUM = readNum;
        READ_SUM = readSum;
        count = new AtomicInteger(0);
    }

    public static void init(int readNum, int readSum, String filePath) {
        FILE_PATH = filePath;
        READ_NUM = readNum;
        READ_SUM = readSum;
        count = new AtomicInteger(0);
    }

    public static List<Doc> readDocsFromCSV() {
        return readDocsFromCSV(FILE_PATH);
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
                if(indices.size() == READ_NUM)
                    break;
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return indices;
    }

    /**
     * 读取文件，调用该方法前需要先执行 init 方法
     */
    public static List<Doc> read() {
        List<Doc> docs = new ArrayList<>();
        List<Future<List<Doc>>> resList = new ArrayList<>();
        int freq = (READ_SUM / READ_NUM) + (READ_SUM % READ_NUM == 0 ? 0 : 1);
        for (int j = 0; j < freq; j++) {
            Future<List<Doc>> future = executor.submit(() -> {
                return readDocsFromCSV();
            });
            resList.add(future);
        }
        // 获取读取结果
        for(Future<List<Doc>> future : resList) {
            List<Doc> docList = null;
            try {
                docList = future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            assert docList != null;
            docs.addAll(docList);
        }
        return docs;
    }
}
