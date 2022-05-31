package team.snof.simplesearch.search.storage;


import team.snof.simplesearch.search.model.dao.IndexPartial;

import java.util.HashMap;

public class IndexPartialStorage {

    public static long getIndexPartialNum() {
    }


    public static String getIndexPartialWord() {
    }

    public static IndexPartial getIndexPartial(String word) {
    }

    public static long getDocLen(String word, long doc_id) {
    }

    public static HashMap<String, Long> getWordDocNum() {
    }

    // TODO 这里存储的时候 需要判断中间表是否已经存在分词word  若已经存在则是对list扩充 否则就新建
    public void saveIndexPartial(IndexPartial indexPartial) {
    }
}
