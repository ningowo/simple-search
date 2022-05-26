package team.snof.simplesearch.search.engine;

import lombok.extern.slf4j.Slf4j;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.dao.index.IndexPartial;

@Slf4j
public class IndexStorage {

    public void saveIndex(Index index) {
        try {
            // 调用云存储或者本地存储
        } catch (Exception e) {
            log.error("");
        }

    }

    public void saveIndexPartial(IndexPartial indexPartial) {
        try {
            // 调用云存储或者本地存储
        } catch (Exception e) {
            log.error("");
        }

    }
}
