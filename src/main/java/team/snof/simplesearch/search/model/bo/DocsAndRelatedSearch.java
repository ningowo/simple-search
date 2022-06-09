package team.snof.simplesearch.search.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import team.snof.simplesearch.search.model.dao.doc.Doc;

import java.util.List;

@Data
@AllArgsConstructor
public class DocsAndRelatedSearch {

    List<Doc> docs;

    List<String> relatedSearches;

}
