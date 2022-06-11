package team.snof.simplesearch.search.model.dao.doc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocInfo implements Serializable {

    private String docId; // 文档的id

    private BigDecimal corr;  // 相关度系数

}
