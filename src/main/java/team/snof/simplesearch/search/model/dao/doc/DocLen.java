package team.snof.simplesearch.search.model.dao.doc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocLen {

    @Id
    private String docId;

    private long docLen;

}
