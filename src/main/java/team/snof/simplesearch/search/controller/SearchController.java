package team.snof.simplesearch.search.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import team.snof.simplesearch.search.model.vo.ResultVO;
import team.snof.simplesearch.search.model.vo.SearchListResponseVO;
import team.snof.simplesearch.search.model.vo.SearchRequestVO;

@Api("搜索接口")
@RestController
public class SearchController {

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ApiOperation("搜索接口")
    public ResultVO<SearchListResponseVO> search(@RequestBody SearchRequestVO request) {

        return ResultVO.newSuccessResult();
    }





}
