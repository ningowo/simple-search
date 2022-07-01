package team.snof.simplesearch.search.adaptor;

import team.snof.simplesearch.search.model.dao.Doc;
import team.snof.simplesearch.search.model.vo.DocVO;
import team.snof.simplesearch.search.model.vo.SearchRequestVO;
import team.snof.simplesearch.search.model.vo.SearchResponseVO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SearchAdaptor {

    public static List<DocVO> convertDocListToDocVOList(List<Doc> docs) {
        return docs.stream().map(DocVO::buildDocVO).collect(Collectors.toList());
    }

    public static SearchResponseVO getEmptyResponseVO(SearchRequestVO request) {
        request.setTotal(0L);
        return SearchResponseVO.builder()
                .docVOList(Collections.emptyList())
                .relatedSearchList(Collections.emptyList())
                .query(request)
                .build();
    }

}
