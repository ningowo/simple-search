package team.snof.simplesearch.search.model.dao.index;

import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public class IndexPartial {
    // 中间表word_temp的数据结构
    private String indexKey;

    public List<TempData> tempDataList;

    public IndexPartial(String indexKey, Long docId, Long wordFreq) {
        this.indexKey = indexKey;
        TempData tempData = new TempData(docId, wordFreq);
        List<TempData> tempDataList = new ArrayList<>();
        tempDataList.add(tempData);
        this.tempDataList = tempDataList;
    }

    public String getIndexKey() {
        return indexKey;
    }

    public void setIndexKey(String indexKey) {
        this.indexKey = indexKey;
    }

    public List<TempData> getTempDataList() {
        return tempDataList;
    }

    public void setTempDataList(List<TempData> tempDataList) {
        this.tempDataList = tempDataList;
    }
}
