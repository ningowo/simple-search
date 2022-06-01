package team.snof.simplesearch.search.storage;


import team.snof.simplesearch.search.model.dao.index.IndexPartial;

import java.util.HashMap;
import java.util.List;

public class IndexPartialStorage {

    public long getIndexPartialNum() {
    }


    public List<String> getAllIndexPartialWord() {
    }

    public IndexPartial getIndexPartial(String word) {
    }


    public long getDocLen(String word, long doc_id) {
    }

    public HashMap<String, Long> getWordDocNum() {
    }

    // TODO 这里存储的时候 需要判断中间表是否已经存在分词word  若已经存在则是对list扩充 否则就新建
    public void saveIndexPartial(IndexPartial indexPartial) {
    }
}
