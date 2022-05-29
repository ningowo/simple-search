package team.snof.simplesearch.search.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import team.snof.simplesearch.search.model.dao.BM25Parameter;

import java.util.HashMap;
import java.util.Map;

@Mapper
public interface BM25ParameterMapper {

    int insertBM25Parameter(BM25Parameter bm25Parameter);

    long getRecordNum();

    // 写法是否有问题？？？
    @MapKey("word")
    HashMap<String, Long> getWordDocNum();

    BM25Parameter getRecord(long id);
}
