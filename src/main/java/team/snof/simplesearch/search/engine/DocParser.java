package team.snof.simplesearch.search.engine;

import org.springframework.beans.factory.annotation.Autowired;
import team.snof.simplesearch.common.util.CSVFileReader;
import team.snof.simplesearch.common.util.SnowflakeIdGenerator;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.model.bo.BM25ParseDocResult;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.dao.index.IndexPartial;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class DocParser {

    private static long docNum = 0;

    private static long docTotalLength;

    @Autowired
    WordSegmentation segmentation;
    @Autowired
    IndexStorage indexStorage;
    @Autowired
    DocStorage docStorage;
//    @Autowired
//    SnowflakeIdGenerator snowflakeIdGenerator;


    private ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 5, 30,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(20, false));

    public static void main(String[] args) {
        // 从csv文件获取Doc
        List<Doc> docList = CSVFileReader.readDocsFromCSV("");

        // 解析并存储索引和文件
        DocParser docParser = new DocParser();
        docParser.parse(docList);
    }

    private void parse(List<Doc> docList) {
        List<Future<String>> resultList = new ArrayList<Future<String>>();

        // 多线程解析doc，并获得索引所需参数
        for (Doc doc : docList) {
            Future<String> future = executor.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return parseDoc("");
                }
            });
            resultList.add(future);
        }

        // TODO 事务，重试，再考虑一下实现方式
        // 构建以及存储索引
        for (int i = 0; i < resultList.size(); i++) {
            Long snowid = SnowflakeIdGenerator.generate();

            Index index = buildIndex();
            indexStorage.saveIndex(index);

            IndexPartial indexPartial = buildIndexPartial("");
            indexStorage.saveIndexPartial(indexPartial);

            docStorage.saveDoc(new Doc());
        }

    }

    // 看看怎么实现?
    private void buildIndexAndIndexPartial() {}


    private IndexPartial buildIndexPartial(String parseResult) {
        return IndexPartial.builder().build();
    }

    private Index buildIndex() {
        return new Index();
    }


    public String parseDoc(String docCaption) {
        BM25ParseDocResult bm25ParseDocResult = BM25ParseDocResult.builder().build();

        // 分词接口
        segmentation.segment("文档里的内容");

        // 逻辑
        // docCaption balabala


        return "";
    }

}
