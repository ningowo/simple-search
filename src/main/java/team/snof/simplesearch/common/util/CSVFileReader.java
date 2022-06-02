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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CSVFileReader {
    // 默认的文件读取位置
    private static final String workDir = System.getProperty("user.dir");
    private static final String FILE_PATH = workDir + "\\data.csv";

    private static final Integer OFFSET_DATA = 2;   // 头两行是标题，需要跳过
    public static Integer READ_NUM = 1000;    // 每一个线程读取的数量
    public static Integer READ_SUM = 80000;   // 总共需要读的条数

    // 线程池参数
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 30;
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000, false));

    // 用于控制文件读取位置的参数
    public static AtomicInteger count;

    /**
     * 从默认的路径读取文件
     */
    public static List<Doc> read() {
        return read(FILE_PATH);
    }

    /**
     * 从文件路径列表中读取文件
     * @param filePathList 文件路径列表
     * @return [Key:文件名，Value:对应文件内的数据列表]
     */
    public static Map<String, List<Doc>> read(List<String> filePathList) {
        HashMap<String, List<Doc>> resList = new HashMap<>();
        for(String filePath : filePathList) {
            List<Doc> docs = read(filePath);
            resList.put(filePath, docs);
        }
        return resList;
    }

    /**
     * 从指定CSV文件路径中读取文件
     * @param filePath 指定的CSV文件路径
     * @return CSV文件的数据内容
     */
    public static List<Doc> read(String filePath) {
        // 读取每一个文件之前，都需要重置count参数，从头开始读取
        count = new AtomicInteger(0);
        List<Doc> docs = new ArrayList<>();
        List<Future<List<Doc>>> resList = new ArrayList<>();

        // 根据 READ_NUM（每一个线程需要读取的条数）和 READ_SUM（总共需要读取的条数）计算出需要的线程数
        int freq = (READ_SUM / READ_NUM) + (READ_SUM % READ_NUM == 0 ? 0 : 1);

        // 然后多个线程同时读取同一个文件
        for (int j = 0; j < freq; j++) {
            Future<List<Doc>> future = executor.submit(() -> {
                return readDocsFromCSV(filePath);
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

    /**
     * 多线程读取同一个文件时，为了避免读取到重复的数据，
     * 需要用count参数来指定不同的线程，读取一个文件的不同位置，
     * 具体开始的读取位置为 OFFSET_DATA + count * READ_NUM
     */
    private static List<Doc> readDocsFromCSV(String filePath) {
        int start = count.getAndIncrement();
        List<Doc> indices = new ArrayList<>();
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath), "GBK");
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // 设置当前线程需要从第几条数据开始读取
            CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(csvParser).withSkipLines(OFFSET_DATA + start * READ_NUM).build();

            // 开始读取文件
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                String url = values[0];
                String caption = values[1];
                indices.add(new Doc(url, caption));
                // 一个线程读取到条数达到指定数量，就可以停止
                if(indices.size() == READ_NUM)
                    break;
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return indices;
    }
}
