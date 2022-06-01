package team.snof.simplesearch.search.model.dao.index;

import lombok.Builder;
import team.snof.simplesearch.search.model.dao.TempData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Builder
public class IndexPartial {
    // 中间表word_temp的数据结构
    private String indexKey;

    public List<TempData> tempDataList;

    public IndexPartial(String indexKey, long docId, long docLen, long wordFreq) {
        this.indexKey = indexKey;
        TempData tempData = new TempData(docId, wordFreq, docLen);
        List<TempData> tempDataList = new ArrayList<>();
        this.tempDataList = tempDataList;
    }
}
