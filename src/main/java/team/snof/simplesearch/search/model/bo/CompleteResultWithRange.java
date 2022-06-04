package team.snof.simplesearch.search.model.bo;

import lombok.Data;
import team.snof.simplesearch.search.model.dao.doc.Doc;

import java.util.List;

@Data
public class CompleteResultWithRange {

    List<Doc> docs;

    List<Long> totalDocIds;

    List<String> relatedSearch;

}