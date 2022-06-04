package team.snof.simplesearch.search.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.snof.simplesearch.search.mapper.favourite.CollectionMapper;
import team.snof.simplesearch.search.mapper.favourite.DatasetMapper;
import team.snof.simplesearch.search.mapper.favourite.FavouriteMapper;
import team.snof.simplesearch.search.model.bo.favorite.Collection;
import team.snof.simplesearch.search.model.bo.favorite.Dataset;
import team.snof.simplesearch.search.model.bo.favorite.Favourite;
import team.snof.simplesearch.search.model.vo.ResultVO;

import java.util.List;

/**
 * @author zhouyg
 * @date 2022/5/28
 */

@Service
public class FavouriteService {

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private DatasetMapper datasetMapper;

    @Autowired
    private FavouriteMapper favouriteMapper;

    //新建收藏夹
    public Object addFavourite(Integer userId, String favouriteName) {

        Wrapper<Favourite> query = new QueryWrapper<Favourite>().lambda()
                .eq(Favourite::getUserId, userId)
                .eq(Favourite::getFavouriteName, favouriteName);
        if (favouriteMapper.selectCount(query) > 0) {
            return ResultVO.newFailedResult("该用户对应的收藏夹已存在");
        }
        favouriteMapper.insert(new Favourite(userId, favouriteName));
        return ResultVO.newSuccessResult(favouriteMapper.selectOne(query));
    }

    //删除收藏夹
    public Object deleteFavourite(Integer userId, String favouriteName) {
        Wrapper<Favourite> queryFavourite = new QueryWrapper<Favourite>().lambda()
                .eq(Favourite::getUserId, userId)
                .eq(Favourite::getFavouriteName, favouriteName);
        if (favouriteMapper.selectCount(queryFavourite) <= 0) {
            return ResultVO.newFailedResult("该用户对应的收藏夹不存在");
        }
        Favourite favourite = favouriteMapper.selectOne(queryFavourite);
        Wrapper<Collection> queryCollect = new QueryWrapper<Collection>().lambda()
                .eq(Collection::getFavouriteId, favourite.getId());
        collectionMapper.delete(queryCollect);

        return ResultVO.newSuccessResult(favouriteMapper.delete(queryFavourite));
    }

    //重命名收藏夹
    public Object renameFavourite(Integer userId, String originFavouriteName, String newFavouriteName) {
        Wrapper<Favourite> update = new UpdateWrapper<Favourite>().lambda()
                .eq(Favourite::getUserId, userId)
                .eq(Favourite::getFavouriteName, originFavouriteName);
        return ResultVO.newSuccessResult(favouriteMapper.update(new Favourite(userId, newFavouriteName), update));
    }

    //显示用户拥有的收藏夹
    public Object showFavourites(Integer userId) {
        List<Favourite> favouriteList = favouriteMapper.selectList(new QueryWrapper<Favourite>().lambda()
                .eq(Favourite::getUserId, userId));
//        System.out.println(favouriteList);
        return favouriteList;
    }

    //显示文章
    public Object showDataInFavourite(Integer favouriteId) {
        Wrapper<Collection> query = new QueryWrapper<Collection>().lambda()
                .eq(Collection::getFavouriteId, favouriteId);
        List<Collection> collectionList = collectionMapper.selectList(query);
        List<Dataset> datasets = datasetMapper.searchDataSet(collectionList);
        return ResultVO.newSuccessResult(datasets);
    }

    //收藏文章
    public Object addDataToFavourite(Integer favouriteId, Integer dataId) {
        Wrapper<Collection> query = new QueryWrapper<Collection>().lambda()
                .eq(Collection::getFavouriteId, favouriteId)
                .eq(Collection::getDataId, dataId);
        if (collectionMapper.selectCount(query) > 0) {
            return ResultVO.newFailedResult("该收藏夹中已存在该记录");
        }
        return ResultVO.newSuccessResult(collectionMapper.insert(new Collection(favouriteId, dataId)));
    }

    //取消文章收藏
    public Object deleteDataFromFavourite(Integer favouriteId, Integer dataId) {
        Wrapper<Collection> query = new QueryWrapper<Collection>().lambda()
                .eq(Collection::getFavouriteId, favouriteId)
                .eq(Collection::getDataId, dataId);
        if (collectionMapper.selectCount(query) < 0) {
            return ResultVO.newFailedResult("该收藏夹中不存在该记录");
        }
        return ResultVO.newSuccessResult(collectionMapper.delete(query));
    }
}
