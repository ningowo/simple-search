package team.snof.simplesearch.search.model.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;

/**
 * 倒排索引
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvertedIndex implements Serializable {

    // 分词
    @Id
    private String word;

    private List<String> docIds;

}
