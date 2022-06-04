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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CSVFileReader {
    // 默认的文件读取根目录
    private static final String workDir = System.getProperty("user.dir");
    private static final String FILE_PATH = workDir + "\\data.csv";
    private static final Integer HEADER_OFFSET = 2;   // 头两行是标题，需要跳过

    // 用于控制文件读取位置的参数
    public static AtomicInteger count;


    // 线程池参数
    private static final int CORE_POOL_SIZE = 8;
    private static final int MAXIMUM_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 30;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TIME_UNIT,
            new ArrayBlockingQueue<>(10, false));

    /**
     * 从默认的路径读取文件
     */
    public static List<Doc> readDefault() {
        return readFile(FILE_PATH);
    }

    /**
     * 读取一个文件到doc列表对象
     */
    public static List<Doc> readFile(String filePath) {
        List<Doc> docs = new ArrayList<>();
        try {
            // 使用CSVParser流式读取
            InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath), "GBK");
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
            CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(csvParser).withSkipLines(HEADER_OFFSET).build();

            // 读取文件
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                String url = values[0];
                String caption = values[1];
                docs.add(new Doc(url, caption));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return docs;
    }

    public static Map<String, List<Doc>> parallelReadFiles(List<String> filePathList) throws InterruptedException {
        return parallelReadFiles("", filePathList);
    }

    /**
     * 多线程读取，适用于多个小文件
     *
     * @param filePathList 文件路径列表
     * @return [Key:文件名，Value:对应文件内的数据列表]
     */
    public static Map<String, List<Doc>> parallelReadFiles(String rootPath, List<String> filePathList) throws InterruptedException {
        HashMap<String, List<Doc>> fileListToDocListsMap = new HashMap<>();
        int fileNum = filePathList.size();

        // 如没有要读取的文件，直接返回
        if (fileNum == 0) {
            return fileListToDocListsMap;
        }

        // 如果只有一个文件，直接简单读取
        if (fileNum == 1) {
            String filePath = filePathList.get(0);
            fileListToDocListsMap.put(filePath, readFile(filePath));
        }

        // 对多个文件进行多线程读取
        CountDownLatch countDownLatch = new CountDownLatch(fileNum);
        // 防止并发添加出现问题
        ConcurrentHashMap<String, Future<List<Doc>>> futureMap = new ConcurrentHashMap<>();

        // 多个线程同时读取多个小文件
        for (String filePath : filePathList) {
            Future<List<Doc>> future = executor.submit(() -> {
                List<Doc> docList = readFile(rootPath + filePath);
                countDownLatch.countDown();
                return docList;
            });
            futureMap.put(filePath, future);
        }

        // 等待所有线程读取完成
        countDownLatch.await();

        // 获取读取结果
        for (Map.Entry<String, Future<List<Doc>>> entry : futureMap.entrySet()) {
            try {
                fileListToDocListsMap.put(entry.getKey(), entry.getValue().get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return fileListToDocListsMap;
    }

}