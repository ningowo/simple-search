package team.snof.simplesearch.search.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.snof.simplesearch.search.model.vo.ResultVO;
import team.snof.simplesearch.search.service.FavouriteService;

/**
 * @author zhouyg
 * @date 2022/5/28
 */

@Api(value = "用户收藏夹")
@RestController()
@RequestMapping("/collect")
public class CollectController {

    @Autowired
    private FavouriteService favouriteService;

    @PostMapping("/add")
    public ResultVO addFavourite(Integer userId, String favouriteName) {
        return (ResultVO) favouriteService.addFavourite(userId, favouriteName);
    }

    @PostMapping("/show")
    public ResultVO showFavourite(Integer userId) {
        return ResultVO.newSuccessResult(favouriteService.showFavourites(userId));
    }

    @PostMapping("/delete")
    public ResultVO deleteFavourite(Integer userId, String favouriteName) {
        return (ResultVO) favouriteService.deleteFavourite(userId, favouriteName);
    }

    @PostMapping("/rename")
    public ResultVO renameFavourite(Integer userId, String originFavouriteName, String newFavouriteName) {
        return (ResultVO) favouriteService.renameFavourite(userId, originFavouriteName, newFavouriteName);
    }

    @PostMapping("/article/show")
    public ResultVO showDataInFavourite(Integer favouriteId) {
        return (ResultVO) favouriteService.showDataInFavourite(favouriteId);
    }

    @PostMapping("/article/add")
    public ResultVO addDataToFavourite(Integer favouriteId, Integer dataId) {
        return (ResultVO) favouriteService.addDataToFavourite(favouriteId, dataId);
    }

    @PostMapping("/article/delete")
    public ResultVO deleteDataFromFavourite(Integer favouriteId, Integer dataId) {
        return (ResultVO) favouriteService.deleteDataFromFavourite(favouriteId, dataId);
    }

}
