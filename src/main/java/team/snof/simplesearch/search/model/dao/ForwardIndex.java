package team.snof.simplesearch.search.model.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 正排索引
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForwardIndex implements Serializable {

    // 文档的id
    @Id
    private String docId;

    // doc长度
    private Long docLength;

    // 分词词频
    private List<WordFreq> wordFreqList;

}
