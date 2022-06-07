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

@Api("搜索接口")
@RestController()
@RequestMapping("/search/test")
public class TestController {

    @Autowired
    IndexGenerateRunner runner;

    @Autowired
    IndexStorage indexStorage;

    @RequestMapping("/parsedoc")
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

    @RequestMapping("/build")
    public ResultVO buildIndex() {

        runner.buildIndex();

        return ResultVO.newSuccessResult();
    }

    @RequestMapping("/find")
    public ResultVO findAllIndex() {
        List<Index> all = indexStorage.findAll();

        return ResultVO.newSuccessResult(all);
    }

    @RequestMapping(value = "/test1", method = RequestMethod.GET)
    public ResultVO<String> test1() {
        return ResultVO.newSuccessResult("OK");
    }


//    @RequestMapping(value = "/eng", method = RequestMethod.GET)
//    public ResultVO<String> engineTest() {
//        System.out.println("+++++++++++");
//
//        List<String> words = new ArrayList<>();
//        words.add("曾是");
//        words.add("妈妈");
//
//        List<Long> docIds = new ArrayList<>();
//        docIds.add(6932693475694469120L);
//        docIds.add(6932695833488785408L);
//        docIds.add(6932695902157930496L);
//
////        List<Index> indices = engine.batchFindIndexes(words);
////        for (Index index : indices) {
////            System.out.println("IND+++++++++++" + index);
////        }
////        Index ind = engine.findIndex("曾是");
////        System.out.println(ind);
//
//        System.out.println("==================================================");
//
//        Map<String, Integer> wordToFreqMap = new HashMap<>();
//        wordToFreqMap.put("曾是", 2);
//        wordToFreqMap.put("妈妈", 1);
//        ComplexEngineResult result = engine.find(wordToFreqMap);
//        System.out.println(result.getDocs());
//        System.out.println(result.getTotalDocIds());
//        System.out.println(result.getRelatedSearch());
//
//        System.out.println("))))))))))))))))))");
//        return ResultVO.newSuccessResult("OK");
//    }
//
//    @RequestMapping("/save")
//    public void save(Index index) {
//
//        List<DocInfo> docInfos = new ArrayList<>();
//        docInfos.add(new DocInfo(987L,new BigDecimal(0.2)));
//        docInfos.add(new DocInfo(987L,new BigDecimal(0.6)));
//        Index ind = new Index("测试1", docInfos);
//
//        mongoTemplate.save(ind, "word_docid_corr");
//    }
//
//    @RequestMapping("/find")
//    public List<Index> findByKey(String key, boolean defaultkey) {
//        if (defaultkey) {
//            Query query = new Query(Criteria.where("indexKey").is("测试1"));
//            List<Index> indices = mongoTemplate.find(query, Index.class);
//            System.out.println(indices);
//            return indices;
//        } else {
//            Query query = new Query(Criteria.where("indexKey").is(key));
//            List<Index> indices = mongoTemplate.find(query, Index.class);
//            System.out.println(indices);
//            return indices;
//        }
//    }
//
//
//    @RequestMapping("/indexstore")
//    public List<Index> indexStore(String key, boolean defaultkey) {
//        String indexKey = "测试1";
//
//        if (!defaultkey) {
//            indexKey = key;
//        }
//
//        List<DocInfo> docInfos = new ArrayList<>();
//        docInfos.add(new DocInfo(987L,new BigDecimal(0.2)));
//        docInfos.add(new DocInfo(987L,new BigDecimal(0.6)));
//        Index ind = new Index(indexKey, docInfos);
//
//        indexStorage.save(ind);
//        System.out.println(ind);
//
//        List<Index> indices = indexStorage.findAll();
//        System.out.println(indices);
//        return indices;
//    }



}
