package team.snof.simplesearch.search.model.dao.index;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Index {

    // 转码过的transcoded中文或英文分词
    public Long IndexKey;

    public List<Pair<Long, BigDecimal>> docIdAndCorrList;

}
