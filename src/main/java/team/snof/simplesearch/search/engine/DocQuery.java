package team.snof.simplesearch.search.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.engine.storage.DocStorage;

@Component
public class DocQuery {
    @Autowired
    DocStorage docStorage;
    public Doc findDocById(Long id){
        //todo:要判是否为空吗
        return docStorage.findById(id).get(0);
    }
}
