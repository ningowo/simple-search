package team.snof.simplesearch.search.model.bo;

import team.snof.simplesearch.search.model.dao.doc.Doc;

import java.util.List;

public class CompleteResultWithRange {
    List<Doc> docs;
    List<Long> totalDocIds;
    List<String> relatedSearch;
}