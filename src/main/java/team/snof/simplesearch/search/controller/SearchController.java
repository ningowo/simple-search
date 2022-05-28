package team.snof.simplesearch.search.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import team.snof.simplesearch.search.model.vo.ResultVO;
import team.snof.simplesearch.search.model.vo.SearchListResponseVO;
import team.snof.simplesearch.search.model.vo.SearchRequestVO;
import team.snof.simplesearch.search.service.SearchService;;

@Api("搜索接口")
@RestController()
@RequestMapping("/search")
public class SearchController {

    @Autowired
    SearchService searchService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiOperation("搜索接口")
    public ResultVO<SearchListResponseVO> search(@RequestBody SearchRequestVO request) {

        return ResultVO.newSuccessResult(searchService.search(request));
    }





}
