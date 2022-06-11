package team.snof.simplesearch.search.model.dao.index;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocInfo implements Serializable {

    // 文档的id
    private String docId;

    // 相关度系数（分词权重*分词文档关联度）
    private BigDecimal corr;

}
