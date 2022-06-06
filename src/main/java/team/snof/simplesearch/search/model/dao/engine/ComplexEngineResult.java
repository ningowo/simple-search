package team.snof.simplesearch.search.model.dao.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import  team.snof.simplesearch.search.model.dao.doc.Doc;

@Data
@AllArgsConstructor
public class ComplexEngineResult {

    //整体查询时返回全部文档，区间查询时返回指定范围内的文档
    List<Doc> docs;

    List<Long> totalDocIds;

    List<String> relatedSearch;

}