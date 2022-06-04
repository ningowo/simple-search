package team.snof.simplesearch.search.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import team.snof.simplesearch.search.model.vo.ResultVO;
import team.snof.simplesearch.search.model.vo.SearchListResponseVO;
import team.snof.simplesearch.search.model.vo.SearchRequestVO;
import team.snof.simplesearch.search.service.SearchService;;import java.util.ArrayList;
import java.util.List;

@Api("搜索接口")
@RestController()
@RequestMapping("/search")
public class SearchController {

    @Autowired
    SearchService searchService;

    @Autowired
    private RedisTemplate redisTemplate;


    @RequestMapping(value = "/s", method = RequestMethod.GET)
    @ApiOperation("搜索接口")
    public ResultVO<SearchListResponseVO> search(@RequestBody SearchRequestVO request) {

        return ResultVO.newSuccessResult(searchService.search(request));
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ApiOperation("测试接口")
    public ResultVO<String> test() {
        return ResultVO.newSuccessResult(searchService.test());
    }

}
