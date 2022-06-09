package team.snof.simplesearch.search.model.bo;

import lombok.Data;

import java.util.List;

@Data
public class QueryValues {

    List<Long> totalDocIds;

    List<String> relatedSearch;

}
