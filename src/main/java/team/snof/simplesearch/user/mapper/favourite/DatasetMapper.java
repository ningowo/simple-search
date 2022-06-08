package team.snof.simplesearch.user.mapper.favourite;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import team.snof.simplesearch.user.model.bo.favorite.Collection;
import team.snof.simplesearch.user.model.bo.favorite.Dataset;

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
