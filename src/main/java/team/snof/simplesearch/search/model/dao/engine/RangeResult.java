package team.snof.simplesearch.search.model.dao.engine;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import  team.snof.simplesearch.search.model.dao.doc.Doc;

@Data
@Builder
public class RangeResult {
    List<Doc> docs;
    List<Long> totalDocIds;
    List<String> relatedSearch;
    public RangeResult(List<Doc> docs, List<Long> totalDocIds, List<String> relatedSearch){
        this.docs = docs;
        this.totalDocIds = totalDocIds;
        this.relatedSearch = relatedSearch;
    }
}
