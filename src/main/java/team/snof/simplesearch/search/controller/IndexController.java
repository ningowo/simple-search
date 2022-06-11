package team.snof.simplesearch.search.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.snof.simplesearch.search.engine.Engine;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.vo.ResultVO;
import team.snof.simplesearch.search.service.IndexGenerateService;
import team.snof.simplesearch.search.storage.IndexStorage;

import java.util.List;

@Api("索引接口")
@RestController()
@RequestMapping("/search/test/index")
public class IndexController {

    @Autowired
    IndexGenerateService indexService;

    @Autowired
    IndexStorage indexStorage;

    @Autowired
    Engine engine;

    @Autowired
    MongoTemplate mongoTemplate;

    @RequestMapping("/parsedoc")
    public ResultVO getAndParseFile(@RequestParam String filePath, @RequestParam boolean defaultPath) {

        String path;
        if (defaultPath) {
            path = "D:\\ByteDanceCamp\\test80.csv";
        } else {
            path = filePath;
        }
        indexService.parseAndStoreDocs(path);

        return ResultVO.newSuccessResult();
    }

    @RequestMapping("/build")
    public ResultVO buildIndex() {

        indexService.buildIndex();

        return ResultVO.newSuccessResult();
    }

    @RequestMapping("/findall")
    public ResultVO findAllIndex() {
        List<Index> all = indexStorage.findAll();

        return ResultVO.newSuccessResult(all);
    }

    @RequestMapping("/findone")
    public List<Index> findByKey(String key, boolean defaultkey) {
        String indexKey = key;
        if (defaultkey) {
            indexKey = "包邮";
        }

        return indexStorage.findByKey(indexKey);
    }

    @RequestMapping(value = "/generate", method = RequestMethod.GET)
    public ResultVO<String> generate(@RequestParam String filePath, @RequestParam boolean defaultPath) throws Exception {

        String path;
        if (defaultPath) {
            path = "D:\\ByteDanceCamp\\test20.csv";
        } else {
            path = filePath;
        }
        System.out.println("开始解析文档");
        indexService.generate(path);

        return ResultVO.newSuccessResult("Result: " + path);
    }

}