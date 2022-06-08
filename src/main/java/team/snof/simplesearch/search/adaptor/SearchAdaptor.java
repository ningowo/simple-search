package team.snof.simplesearch.search.adaptor;

import org.springframework.stereotype.Component;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.vo.DocVO;

import java.util.List;
import java.util.stream.Collectors;

public class SearchAdaptor {

    public static List<DocVO> convertDocListToDocVOList(List<Doc> docs) {
        return docs.stream().map(DocVO::buildDocVO).collect(Collectors.toList());
    }

}
