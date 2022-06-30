package team.snof.simplesearch.search.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.snof.simplesearch.search.engine.Engine;
import team.snof.simplesearch.search.model.dao.InvertedIndex;
import team.snof.simplesearch.search.model.vo.ResultVO;
import team.snof.simplesearch.search.service.IndexGenerateService;

import java.util.List;

@Api("索引接口")
@RestController()
@RequestMapping("/search/index")
public class IndexController {

    @Autowired
    IndexGenerateService indexService;

    @RequestMapping(value = "/parse", method = RequestMethod.GET)
    public ResultVO<String> parse(@RequestParam String filePath, @RequestParam boolean defaultPath) {

        String path;
        if (defaultPath) {
            path = "D:\\ByteDanceCamp\\test2k.csv";
        } else {
            path = filePath;
        }
        System.out.println("开始解析文档");
        indexService.generate(path);

        return ResultVO.newSuccessResult("Result: " + path);
    }

}