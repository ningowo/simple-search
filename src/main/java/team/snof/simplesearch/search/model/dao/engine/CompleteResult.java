package team.snof.simplesearch.search.model.dao.engine;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import team.snof.simplesearch.search.model.dao.doc.Doc;

@Data
@Builder
public class CompleteResult {
    List<Doc> totalDocs;
    List<Long> totalDocIds;
    List<String> relatedSearch;
    public CompleteResult(List<Doc> totalDocs, List<Long> totalDocIds, List<String> relatedSearch){
        this.totalDocs = totalDocs;
        this.totalDocIds = totalDocIds;
        this.relatedSearch = relatedSearch;
    }
}
