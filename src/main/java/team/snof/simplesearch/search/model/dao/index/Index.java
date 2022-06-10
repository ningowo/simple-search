package team.snof.simplesearch.search.model.dao.index;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import team.snof.simplesearch.search.model.dao.doc.DocInfo;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Index implements Serializable {
    public String indexKey; // 分词

    public List<DocInfo> docInfoList;

}
