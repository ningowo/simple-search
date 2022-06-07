package team.snof.simplesearch.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.snof.simplesearch.search.engine.IndexGenerateRunner;
import team.snof.simplesearch.search.model.vo.ResultVO;

@RestController
@RequestMapping("/search/index")
public class IndexBuildController {

    @Autowired
    IndexGenerateRunner runner;

    @RequestMapping("/parse")
    public ResultVO getAndParseFile(String path) throws Exception {

        runner.parseAndStoreDocs(path);

        return ResultVO.newSuccessResult();
    }

    @RequestMapping("/build")
    public ResultVO buildIndex() throws Exception {

        runner.buildIndex();

        return ResultVO.newSuccessResult();
    }

}
