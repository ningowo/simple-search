package team.snof.simplesearch.search.model.dao.index;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 倒排索引中的文档信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocInfo {
    private Long docId; // 文档的id
    private Long freq;  // 词频
    private BigDecimal corr;  // 相关度系数
}
