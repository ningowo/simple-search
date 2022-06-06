package team.snof.simplesearch.search.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.SnowFlakeIDGenerator;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.doc.DocLen;
import team.snof.simplesearch.search.storage.DocLenStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class IndexGenerateRunner {

    @Autowired
    DocParser docParser;

    @Autowired
    WordSegmentation wordSegmentation;

    @Autowired
    IndexBuilder indexBuilder;

    @Autowired
    DocLenStorage docLenStorage;

//    @Autowired
//    MongoTemplate mongoTemplate;

//    @Autowired
//    TestStorage testStorage;

    String tigerRoot = "D:/GoCamp/wukong_release/";

    public void generate(String path) throws Exception {

        // 从csv文件获取Doc
//        List<Doc> docList = CSVFileReader.readFile(path);
        List<Doc> docList = new ArrayList<>();
        docList.add(new Doc(0L,"https://pic.rmb.bdstatic.com/19539b3b1a7e1daee93b0f3d99b8e795.png", "曾是名不见经传的王平,为何能够取代魏延,成为蜀汉"));
        docList.add(new Doc(0L,"ttps://img14.360buyimg.com/pop/jfs/t1/195513/12/7430/162252/60c1dddcEe8663dfb/d2ed539adcaf2eb0.jpg", "塞下南相中老年人女装夏装短袖t恤衣服岁老人妈妈连衣裙奶奶雪纺套装"));

        // 解析文件和存储文件
        docParser.parse(docList);
        System.out.println("解析文件和存储文件");


//        // 构建索引并存储
        indexBuilder.buildIndexes();
        System.out.println("构建索引并存储");
    }

//    public void generateMultiple(String root, String path) throws Exception {
//        // 获取地址列表
//        File file = new File(root);
//        List<String> fileNameList = List.of(file.list());
//
//        // 从csv文件获取Doc
//        for (String fileName: fileNameList) {
//            String filePath = root + fileName;
//            List<Doc> docList = CSVFileReader.readFile(filePath);
//
//            // 解析文件和存储文件
//            docParser.parse(docList);
//            System.out.println("解析文件和存储文件");
//        }
//
//        // 构建索引并存储
//        indexBuilder.buildIndexes();
//        System.out.println("构建索引并存储");
//    }
}
