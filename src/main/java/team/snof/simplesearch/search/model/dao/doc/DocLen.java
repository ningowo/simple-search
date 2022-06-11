package team.snof.simplesearch.search.model.dao.doc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocLen {

    // 作为MongoDB的_id存储
    @Id
    private String docId;

    private long docLen;

}
