package team.snof.simplesearch.search.mapper.favourite;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import team.snof.simplesearch.search.model.dao.favorite.Collection;
import team.snof.simplesearch.search.model.dao.favorite.Dataset;

import java.util.List;

/**
 *  Mapper
 *
 * @author Zhouyg
 * @date 2022-05-28
 */
@Repository
public interface DatasetMapper extends BaseMapper<Dataset> {

    List<Dataset> searchDataSet(List<Collection> collectionList);
}
