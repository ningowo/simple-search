package team.snof.simplesearch.search.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import team.snof.simplesearch.search.engine.Engine;
import team.snof.simplesearch.search.model.vo.ResultVO;
import team.snof.simplesearch.search.storage.ForwardIndexStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api("测试接口")
@RestController()
@RequestMapping("/search/test")
public class TestController {

    @Autowired
    Engine engine;

    @Autowired
    ForwardIndexStorage forwardIndexStorage;

    @RequestMapping(value = "/forwardBatchFind", method = RequestMethod.GET)
    public ResultVO<List> testMongo(List<String> docIds, boolean useDefault) {
        List<String> ids = List.of("6941014511083630592", "6941014511192682496", "6941014511305928704");

        ids = useDefault ? ids : docIds;

       return ResultVO.newSuccessResult(forwardIndexStorage.batchFind(ids));
    }

    @RequestMapping(value = "/forwardFind", method = RequestMethod.GET)
    public ResultVO testMongo1(String docId, boolean useDefault) {
        String id = "6941014511305928704";

        id = useDefault ? id : docId;

        return ResultVO.newSuccessResult(forwardIndexStorage.find(id));
    }

    @RequestMapping(value = "/eng", method = RequestMethod.GET)
    public ResultVO<String> engineTest() {
        System.out.println("+++++++++++");

        List<String> words = new ArrayList<>();
        words.add("冰箱");

        List<Long> docIds = new ArrayList<>();
        docIds.add(6932693475694469120L);
        docIds.add(6932695833488785408L);
        docIds.add(6932695902157930496L);

        System.out.println("=============================");

        Map<String, Integer> wordToFreqMap1 = new HashMap<>();
        wordToFreqMap1.put("冰箱", 1);
        wordToFreqMap1.put("好玩", 1);
        List<String> sortedDocIds = engine.findSortedDocIds(wordToFreqMap1);
        System.out.println(sortedDocIds);
        System.out.println("+++++++++=============================");

        return ResultVO.newSuccessResult("OK");
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ApiOperation("测试接口")
    public ResultVO<String> test() {
        return ResultVO.newSuccessResult("测试接口ok: ");
    }
}
