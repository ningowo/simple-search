package team.snof.simplesearch.search.model.dao.index;

import lombok.Data;

import java.util.List;

@Data
public class IndexPartial {
    // 中间表word_temp的数据结构
    public String indexKey;

    public List<TempData> tempDataList;

    public IndexPartial(String indexKey, List<TempData> tempDataList) {
        this.indexKey = indexKey;
        this.tempDataList = tempDataList;
    }
}
