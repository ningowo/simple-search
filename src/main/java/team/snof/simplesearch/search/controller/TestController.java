package team.snof.simplesearch.search.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.snof.simplesearch.search.engine.IndexGenerateRunner;
import team.snof.simplesearch.search.engine.TestStorage;
import team.snof.simplesearch.search.model.vo.ResultVO;

@Api("搜索接口")
@RestController()
@RequestMapping("/search")
public class TestController {

    @Autowired
    IndexGenerateRunner runner;

    @RequestMapping(value = "/test1", method = RequestMethod.GET)
    public ResultVO<String> buildIndex() throws Exception {

//        IndexGenerateRunner runner = new IndexGenerateRunner();
//        String path = "D:\\ByteDanceCamp\\test500.csv";
//        runner.generate(path);


//        DocLen docLen = new DocLen(12L, 123L);
//        mongoTemplate.save(docLen, "doc_len");
        TestStorage testStorage = new TestStorage();
        testStorage.test();
        System.out.println("end test1111111111");

        return ResultVO.newSuccessResult("OK ");
    }

    @RequestMapping(value = "/buildind", method = RequestMethod.GET)
    public ResultVO<String> buildIndex(@RequestParam String filePath, @RequestParam boolean defaultPath) throws Exception {

        String path;
        if (defaultPath) {
            path = "D:\\ByteDanceCamp\\wukong50k_release.csv";
        } else {
            path = filePath;
        }

        runner.generate(filePath);

        return ResultVO.newSuccessResult(": " + path);
    }

}
