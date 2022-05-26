package team.snof.simplesearch.search.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.service.DocService;
import team.snof.simplesearch.utils.CSVFileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Component
public class DocPaser {
    private final int CORE_POOL_SIZE = 5;
    private final int MAXIMUM_POOL_SIZE = 10;
    private final long KEEP_ALIVE_TIME = 60;
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000)
    );
    private static final int freq = CSVFileReader.READ_SUM / CSVFileReader.READ_NUM;

    @Autowired
    DocService docService;

    public void parse() {
        List<Future<Boolean>> list = new ArrayList<>();
        for(int i = 0; i < freq; i++) {
            Future<Boolean> res = null;
            res = executor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    List<Doc> docs = CSVFileReader.read();
                    if(docs.isEmpty()) return false;
                    docService.insert(docs);
                    return true;
                }
            });
            list.add(res);
        }
        int cnt = 0;
        for(Future<Boolean> res : list) {
            try {
                if(res.get())
                    cnt++;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        log.info(cnt * CSVFileReader.READ_NUM + " docs insert");
    }
}
