package team.snof.simplesearch.search.engine;

import lombok.extern.slf4j.Slf4j;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.index.Index;

@Slf4j
public class DocStorage {

    public void saveDoc(Doc doc) {
        try {
            // 调用云存储或者本地存储
        } catch (Exception e) {
            log.error("");
        }

    }

}
