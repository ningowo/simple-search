package team.snof.simplesearch.user.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.schema.Collections;
import team.snof.simplesearch.search.model.vo.DatasetVO;
import team.snof.simplesearch.search.model.vo.FavouriteVO;
import team.snof.simplesearch.search.model.vo.ResultVO;
import team.snof.simplesearch.user.mapper.favourite.CollectionMapper;
import team.snof.simplesearch.user.mapper.favourite.DatasetMapper;
import team.snof.simplesearch.user.mapper.favourite.FavouriteMapper;
import team.snof.simplesearch.user.model.bo.favorite.Collection;
import team.snof.simplesearch.user.model.bo.favorite.Dataset;
import team.snof.simplesearch.user.model.bo.favorite.Favourite;

import javax.management.InstanceAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public FavouriteVO addFavourite(Integer userId, String favouriteName) throws InstanceAlreadyExistsException {

        Wrapper<Favourite> query = new QueryWrapper<Favourite>().lambda()
                .eq(Favourite::getUserId, userId)
                .eq(Favourite::getFavouriteName, favouriteName);
        if (favouriteMapper.selectCount(query) > 0) {
            throw new InstanceAlreadyExistsException("该用户对应的收藏夹已存在");
        }
        favouriteMapper.insert(new Favourite(userId, favouriteName));
        return FavouriteVO.buildFavouriteVO(favouriteMapper.selectOne(query));
    }

    //删除收藏夹
    public ResultVO deleteFavourite(Integer userId, String favouriteName) throws NotFoundException {
        Wrapper<Favourite> queryFavourite = new QueryWrapper<Favourite>().lambda()
                .eq(Favourite::getUserId, userId)
                .eq(Favourite::getFavouriteName, favouriteName);
        if (favouriteMapper.selectCount(queryFavourite) <= 0) {
            throw new NotFoundException("收藏夹不存在");
        }
        Favourite favourite = favouriteMapper.selectOne(queryFavourite);
        //删除收藏夹
        favouriteMapper.delete(queryFavourite);
        Wrapper<Collection> queryCollect = new QueryWrapper<Collection>().lambda()
                .eq(Collection::getFavouriteId, favourite.getId());
        //删除收藏夹内对应的记录
        collectionMapper.delete(queryCollect);
        return ResultVO.newSuccessResult("删除成功");
    }

    //重命名收藏夹
    public ResultVO renameFavourite(Integer userId, String originFavouriteName, String newFavouriteName) throws NotFoundException {
        Wrapper<Favourite> queryFavourite = new UpdateWrapper<Favourite>().lambda()
                .eq(Favourite::getUserId, userId)
                .eq(Favourite::getFavouriteName, originFavouriteName);
        if (favouriteMapper.selectCount(queryFavourite) <= 0) {
            throw new NotFoundException("收藏夹不存在");
        }
        favouriteMapper.update(new Favourite(userId, newFavouriteName), queryFavourite);
        return ResultVO.newSuccessResult("更新成功");
    }

    //显示用户拥有的收藏夹
    public List<FavouriteVO> showFavourites(Integer userId) {
        List<Favourite> favouriteList = favouriteMapper.selectList(new QueryWrapper<Favourite>().lambda()
                .eq(Favourite::getUserId, userId));
        List<FavouriteVO> favouriteVOList = favouriteList.stream().map(FavouriteVO::buildFavouriteVO).collect(Collectors.toList());
        return favouriteVOList;
    }

    //显示文章
    public List<DatasetVO> showDataInFavourite(Integer favouriteId) {
        Wrapper<Collection> query = new QueryWrapper<Collection>().lambda()
                .eq(Collection::getFavouriteId, favouriteId);
        List<Collection> collectionList = collectionMapper.selectList(query);
        List<Dataset> datasetList = new ArrayList<>();
        if (collectionList != null && collectionList.size() != 0)
            datasetList = datasetMapper.searchDataSet(collectionList);
        List<DatasetVO> datasetVOList = datasetList.stream().map(DatasetVO::buildDatasetVO).collect(Collectors.toList());
        return datasetVOList;
    }

    //收藏文章
    public ResultVO addDataToFavourite(Integer favouriteId, Integer dataId) throws InstanceAlreadyExistsException {
        Wrapper<Collection> query = new QueryWrapper<Collection>().lambda()
                .eq(Collection::getFavouriteId, favouriteId)
                .eq(Collection::getDataId, dataId);
        if (collectionMapper.selectCount(query) > 0) {
            throw new InstanceAlreadyExistsException("该收藏夹中已存在该记录");
        }
        collectionMapper.insert(new Collection(favouriteId, dataId));
        return ResultVO.newSuccessResult("收藏成功");
    }

    //取消文章收藏
    public ResultVO deleteDataFromFavourite(Integer favouriteId, Integer dataId) throws NotFoundException {
        Wrapper<Collection> query = new QueryWrapper<Collection>().lambda()
                .eq(Collection::getFavouriteId, favouriteId)
                .eq(Collection::getDataId, dataId);
        if (collectionMapper.selectCount(query) < 0) {
            throw new NotFoundException("该收藏夹中不存在该记录");
        }
        collectionMapper.delete(query);
        return ResultVO.newSuccessResult("取消收藏成功");
    }
}
