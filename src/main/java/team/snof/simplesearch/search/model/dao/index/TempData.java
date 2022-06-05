package team.snof.simplesearch.search.model.dao.index;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TempData {
    private long docId;
    private long wordFreq;
}
