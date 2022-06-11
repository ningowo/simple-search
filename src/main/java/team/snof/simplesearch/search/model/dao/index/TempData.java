package team.snof.simplesearch.search.model.dao.index;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TempData {

    // 作为MongoDB的_id存储
    @Id
    private String docId;

    // 分词在文档中词频
    private Integer wordFreq;

}
