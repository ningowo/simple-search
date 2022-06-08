package team.snof.simplesearch.search.model.dao.doc;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
// 文档类用于排序，{docID,similarity}
public class Doc4Sort implements  Comparable<Doc4Sort> {
    private Long DocId;
    private BigDecimal similarity;
    public Doc4Sort(Long docId,BigDecimal similarity){
        this.DocId = docId;
        this.similarity = similarity;
    }
    @Override
    public int compareTo(Doc4Sort y){
        return similarity.compareTo(y.similarity);
    }
}