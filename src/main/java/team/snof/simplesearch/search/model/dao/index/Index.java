package team.snof.simplesearch.search.model.dao.index;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Index {
    public String indexKey;

    public List<DocInfo> docInfoList;

    public Index() {
        this.indexKey = "";
        this.docInfoList = new ArrayList<>();
    }
}
