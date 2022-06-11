package team.snof.simplesearch.search.model.dao.index;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 *  中间表word_temp的dao
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexPartial {

    private String indexKey;

    private List<TempData> tempDataList;

    public IndexPartial(String indexKey, TempData tempData) {
        List<TempData> tempDataList = new ArrayList<>();
        tempDataList.add(tempData);

        this.tempDataList = tempDataList;
        this.indexKey = indexKey;
    }
}
