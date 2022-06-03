package team.snof.simplesearch.search.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.engine.storage.IndexStorage;
import team.snof.simplesearch.search.model.dao.index.Index;

//todo:实现连接池时两个连接共享一个连接池？
@Component
public class IndexQuery {
    @Autowired
    IndexStorage indexStorage;
    public Index findIndexByKey(String Key){
        //todo:要判是否为空吗
        return indexStorage.findByKey(Key).get(0);
    }
}
