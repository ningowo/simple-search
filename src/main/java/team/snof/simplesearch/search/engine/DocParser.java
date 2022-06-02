package team.snof.simplesearch.search.engine;

import org.springframework.beans.factory.annotation.Autowired;
import team.snof.simplesearch.common.util.CSVFileReader;
import team.snof.simplesearch.common.util.SnowflakeIdGenerator;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.engine.storage.DocStorage;
import team.snof.simplesearch.search.engine.storage.IndexStorage;
import team.snof.simplesearch.search.engine.storage.MetaDataStorage;
import team.snof.simplesearch.search.model.bo.BM25ParseDocResult;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.dao.index.IndexPartial;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class DocParser {
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 30;
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000, false));

    @Autowired
    WordSegmentation segmentation;
    @Autowired
    IndexStorage indexStorage;
    @Autowired
    DocStorage docStorage;
    @Autowired
    MetaDataStorage metaDataStorage;
    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;

    public static void main(String[] args) {
        String filePath = "";
        List<Doc> docs = CSVFileReader.read(filePath);
        DocParser docParser = new DocParser();
        docParser.parse(docs);
    }

    private void parse(List<Doc> docList) {
        List<Future<List<Index>>> resList = new ArrayList<>();
        // 多线程解析doc，并获得索引所需参数
        for(Doc doc : docList) {
            Future<List<Index>> future = executor.submit(new Callable<List<Index>>() {
                @Override
                public List<Index> call() throws Exception {
                    // 设置doc的唯一id
                    Long snowId = snowflakeIdGenerator.generate();
                    doc.setSnowflakeDocId(snowId);
                    // 将doc存入数据库
                    docStorage.save(doc);
                    // 修改总文档信息
                    metaDataStorage.addDoc(doc);
                    // 解析doc
                    return parseDoc(doc.getCaption());
                }
            });
            resList.add(future);
        }

        // 通过res.get()方法阻塞主线程，直到解析完成，获取结果
        for (Future<List<Index>> res : resList) {
            List<Index> indices = null;
            try {
                indices = res.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            indexStorage.save(indices);
        }
    }

    // 看看怎么实现?
    private void buildIndexAndIndexPartial() {
    }


    private IndexPartial buildIndexPartial(String parseResult) {
        return IndexPartial.builder().build();
    }

    private Index buildIndex() {
        return new Index();
    }


    public List<Index> parseDoc(String docCaption) {
        BM25ParseDocResult bm25ParseDocResult = BM25ParseDocResult.builder().build();

        // 分词接口
        List<String> terms = segmentation.segment(docCaption);


        return null;
    }

}
