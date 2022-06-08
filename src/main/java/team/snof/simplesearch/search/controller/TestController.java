package team.snof.simplesearch.search.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.snof.simplesearch.search.engine.Engine;
import team.snof.simplesearch.search.engine.IndexGenerateRunner;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.doc.DocInfo;
import team.snof.simplesearch.search.model.dao.engine.ComplexEngineResult;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.vo.ResultVO;
import team.snof.simplesearch.search.storage.IndexStorage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用说明：
 * 1. 开启redis和mongodb
 * 2. 启动项目
 * 3. 下载数据集并放到指定位置
 *      3.1 放到"D:\\ByteDanceCamp\\test20.csv"，然后直接访问http://localhost:8080/search/test/generate?filePath=&defaultPath=true
 *      3.2 或者，放到随便什么位置，然后访问http://localhost:8080/search/test/generate?filePath=${随便什么位置}&defaultPath=false
 *      然后能看到console打印的"解析文件和存储文件、构建索引并存储"，即为成功
 * 4. 访问http://localhost:8080/search/test/eng，即可简单测试engine.find和engine.rangeFind这两个接口
 * 5. （可选）自定义测试方法，修改engineTest方法的参数和内容，自己在controller里传参进行测试
 */
@Api("搜索接口")
@RestController()
@RequestMapping("/search/test")
public class TestController {

    @Autowired
    IndexGenerateRunner runner;

    @Autowired
    IndexStorage indexStorage;

    @Autowired
    Engine engine;

    @Autowired
    MongoTemplate mongoTemplate;

    @RequestMapping("/index/parsedoc")
    public ResultVO getAndParseFile(@RequestParam String filePath, @RequestParam boolean defaultPath) throws Exception {

        String path;
        if (defaultPath) {
            path = "D:\\ByteDanceCamp\\test20.csv";
        } else {
            path = filePath;
        }
        runner.parseAndStoreDocs(path);

        return ResultVO.newSuccessResult();
    }

    @RequestMapping("/index/build")
    public ResultVO buildIndex() {

        runner.buildIndex();

        return ResultVO.newSuccessResult();
    }

    @RequestMapping("/index/findall")
    public ResultVO findAllIndex() {
        List<Index> all = indexStorage.findAll();

        return ResultVO.newSuccessResult(all);
    }

    @RequestMapping("/findind")
    public List<Index> findByKey(String key, boolean defaultkey) {
        String indexKey = key;
        if (defaultkey) {
            indexKey = "测试1";
        }
        List<Index> inds = indexStorage.findByKey(indexKey);

        return inds;
    }

    @RequestMapping(value = "/generate", method = RequestMethod.GET)
    public ResultVO<String> generate(@RequestParam String filePath, @RequestParam boolean defaultPath) throws Exception {

        String path;
        if (defaultPath) {
            path = "D:\\ByteDanceCamp\\test20.csv";
        } else {
            path = filePath;
        }

        runner.generate(path);

        return ResultVO.newSuccessResult("Result: " + path);
    }

    @RequestMapping(value = "/eng", method = RequestMethod.GET)
    public ResultVO<String> engineTest(@RequestParam String word) {
        System.out.println("+++++++++++");

        List<String> words = new ArrayList<>();
        words.add("曾是");
        words.add("妈妈");

        List<Long> docIds = new ArrayList<>();
        docIds.add(6932693475694469120L);
        docIds.add(6932695833488785408L);
        docIds.add(6932695902157930496L);

//        List<Index> indices = engine.batchFindIndexes(words);
//        for (Index index : indices) {
//            System.out.println("IND+++++++++++" + index);
//        }
//        Index ind = engine.findIndex("曾是");
//        System.out.println(ind);

//        System.out.println("==================================================");
//
//        Map<String, Integer> wordToFreqMap = new HashMap<>();
//        wordToFreqMap.put("曾是", 2);
//        wordToFreqMap.put("妈妈", 1);
//        ComplexEngineResult result = engine.find(wordToFreqMap);
//        System.out.println(result.getDocs());
//        System.out.println(result.getTotalDocIds());
//        System.out.println(result.getRelatedSearch());

        System.out.println("=============================");

        Map<String, Integer> wordToFreqMap1 = new HashMap<>();
        wordToFreqMap1.put(word, 2);
        ComplexEngineResult result1 = engine.rangeFind(wordToFreqMap1, 0, 2);
        System.out.println(result1.getDocs());
        System.out.println(result1.getTotalDocIds());
        System.out.println(result1.getRelatedSearch());
        System.out.println("+++++++++=============================");

        return ResultVO.newSuccessResult("OK");
    }

    @RequestMapping(value = "/test1", method = RequestMethod.GET)
    public ResultVO<String> test1() {
        return ResultVO.newSuccessResult("OK");
    }
}
