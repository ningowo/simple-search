package team.snof.simplesearch.search.model.dao.doc;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
// 分词类用于关键词排序，{word,tf-idf}
public class Word4Sort implements  Comparable<Word4Sort> {
    private String word;
    private BigDecimal tf_idf;
    public Word4Sort(String word,BigDecimal tf_idf){
        this.word = word;
        this.tf_idf = tf_idf;
    }
    public int compareTo(Word4Sort y){
        return tf_idf.compareTo(y.tf_idf);
    }
}