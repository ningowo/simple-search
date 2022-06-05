package team.snof.simplesearch.search.controller;

import io.swagger.annotations.Api;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import team.snof.simplesearch.search.model.vo.FavouriteVO;
import team.snof.simplesearch.search.model.vo.ResultVO;
import team.snof.simplesearch.search.service.FavouriteService;

import javax.management.InstanceAlreadyExistsException;

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
        if (favouriteName == null) return ResultVO.newParamErrorResult("收藏夹名称不可为空！");
        FavouriteVO favouriteVO = null;
        try {
            favouriteVO = favouriteService.addFavourite(userId, favouriteName);
        } catch (InstanceAlreadyExistsException e) {
            return ResultVO.newFailedResult(e.getMessage());
        }
        return ResultVO.newSuccessResult(favouriteVO);
    }

    @PostMapping("/show")
    public ResultVO showFavourite(Integer userId) {
        return ResultVO.newSuccessResult(favouriteService.showFavourites(userId));
    }

    @PostMapping("/delete")
    public ResultVO deleteFavourite(Integer userId, String favouriteName) {
        ResultVO resultVO = null;
        try {
            resultVO = favouriteService.deleteFavourite(userId, favouriteName);
        } catch (NotFoundException e) {
            return ResultVO.newFailedResult(e.getMessage());
        }
        return resultVO;
    }

    @PostMapping("/rename")
    public ResultVO renameFavourite(Integer userId, String originFavouriteName, String newFavouriteName) {
        if (originFavouriteName == null || newFavouriteName == null) return ResultVO.newParamErrorResult("收藏夹新名称不可为空！");
        ResultVO resultVO = null;
        try {
            resultVO = favouriteService.renameFavourite(userId, originFavouriteName, newFavouriteName);
        } catch (NotFoundException e) {
            return ResultVO.newFailedResult(e.getMessage());
        }
        return resultVO;
    }

    @PostMapping("/article/show")
    public ResultVO showDataInFavourite(Integer favouriteId) {
        if (favouriteId == null) return ResultVO.newParamErrorResult("请选中收藏夹");
        return ResultVO.newSuccessResult(favouriteService.showDataInFavourite(favouriteId));
    }

    @PostMapping("/article/add")
    public ResultVO addDataToFavourite(Integer favouriteId, Integer dataId) {
        if (favouriteId == null) return ResultVO.newParamErrorResult("请选中收藏夹后操作");
        if (dataId == null) return ResultVO.newParamErrorResult("目标文档不得为空");
        ResultVO resultVO = new ResultVO();
        try {
            resultVO = favouriteService.addDataToFavourite(favouriteId, dataId);
        } catch (InstanceAlreadyExistsException e) {
            return ResultVO.newFailedResult(e.getMessage());
        }
        return resultVO ;
    }

    @PostMapping("/article/delete")
    public ResultVO deleteDataFromFavourite(Integer favouriteId, Integer dataId) {
        if (favouriteId == null) return ResultVO.newParamErrorResult("请选中收藏夹后操作");
        if (dataId == null) return ResultVO.newParamErrorResult("目标文档不得为空");
        ResultVO resultVO = new ResultVO();
        try {
            resultVO = favouriteService.deleteDataFromFavourite(favouriteId, dataId);
        } catch (NotFoundException e) {
            return ResultVO.newFailedResult(e.getMessage());
        }
        return resultVO;
    }

}
