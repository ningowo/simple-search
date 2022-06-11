package team.snof.simplesearch.search.model.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
// 文档类用于排序，{docID,similarity}
public class Doc4Sort implements  Comparable<Doc4Sort> {

    private String DocId;

    private BigDecimal similarity;

    @Override
    public int compareTo(Doc4Sort y){
        return similarity.compareTo(y.similarity);
    }

}