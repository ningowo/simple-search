package team.snof.simplesearch.search.model.bo;

import lombok.Data;
import team.snof.simplesearch.search.model.dao.doc.Doc;

import java.util.List;

@Data
public class CompleteResult {
    List<Doc> totalDocs;
    List<Long> totalDocIds;
    List<String> relatedSearch;
}