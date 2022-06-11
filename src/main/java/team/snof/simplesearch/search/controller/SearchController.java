package team.snof.simplesearch.search.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import team.snof.simplesearch.search.model.vo.ResultVO;
import team.snof.simplesearch.search.model.vo.SearchRequestVO;
import team.snof.simplesearch.search.model.vo.SearchResponseVO;
import team.snof.simplesearch.search.service.SearchService;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@Api("搜索接口")
@RestController()
public class SearchController {

    @Autowired
    SearchService searchService;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ApiOperation("搜索接口")
    public ResultVO search(@Valid SearchRequestVO request) {
        if (request.getQuery().isBlank()) {
            return ResultVO.newParamErrorResult("查询文字不能为空！");
        }

        log.info("开始查询：" + request);

        SearchResponseVO searchResult = null;
        try {
            searchResult = searchService.search(request);
        } catch (IllegalArgumentException e) {
            return ResultVO.newFailedResult(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert searchResult != null;
        log.info("查询完毕：" + request +
                "\n结果长度为：" + searchResult.getDocVOList().size() +
                "\n相关搜索为: " + searchResult.getRelatedSearchList());

        return ResultVO.newSuccessResult(searchResult);
    }

    @RequestMapping(value = "/search/test", method = RequestMethod.GET)
    @ApiOperation("测试接口")
    public ResultVO<String> test() {
        return ResultVO.newSuccessResult("测试接口ok: ");
    }

}
