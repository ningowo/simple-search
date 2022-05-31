package team.snof.simplesearch.search.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DocLenMapper {
    int insertDocLen(DocLen docLen);

    long getDocAveLen();

    long getDocNum();

    long getDocLen(long doc_id);
}
