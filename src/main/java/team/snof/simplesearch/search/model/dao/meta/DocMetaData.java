package team.snof.simplesearch.search.model.dao.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 记录总文档的信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocMetaData {
    private Long docNum;    // 文档总数目
    private Long docLen;    // 文档总长度
    private Long avgLen;    // 文档的平均长度
}
