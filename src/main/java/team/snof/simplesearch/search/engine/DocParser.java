package team.snof.simplesearch.search.engine;

import org.springframework.beans.factory.annotation.Autowired;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.model.bo.BM25ParseDocResult;
import team.snof.simplesearch.search.model.dao.doc.Doc;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DocParser {

    private static long docNum = 0;

    private static long docTotalLength;

    @Autowired
    WordSegmentation segmentation;

    ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 5, 30,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(20, false));

    public void parseDocs(List<Doc> docList) {
        for (Doc doc : docList) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    parseDoc(doc.caption);
                }
            });
        }

    }

    public BM25ParseDocResult parseDoc(String docCaption) {
        BM25ParseDocResult bm25ParseDocResult = BM25ParseDocResult.builder().build();

        // 分词接口
        segmentation.segment("文档里的内容");

        // 逻辑
        // docCaption balabala


        return bm25ParseDocResult;
    }

}
