package team.snof.simplesearch.search.model.dao;

import lombok.Data;

@Data
public class BM25Parameter {
    // 分词
    private String word;

    // doc_id
    private long doc_id;

    // 词频
    private long word_freq;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public long getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(long doc_id) {
        this.doc_id = doc_id;
    }

    public long getWord_freq() {
        return word_freq;
    }

    public void setWord_freq(long word_freq) {
        this.word_freq = word_freq;
    }
}
