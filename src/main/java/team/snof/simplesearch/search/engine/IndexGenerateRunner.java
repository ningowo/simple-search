package team.snof.simplesearch.search.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.CSVFileReader;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.storage.DocLenStorage;

import java.io.File;
import java.util.List;

@Slf4j
@Component
public class IndexGenerateRunner {

    @Autowired
    DocParser docParser;

    @Autowired
    IndexBuilder indexBuilder;

    String tigerRoot = "D:/GoCamp/wukong_release/";

    public void generate(String path) throws Exception {

        // 从csv文件获取Doc
        List<Doc> docList = CSVFileReader.readFile(path);

        // 解析文件和存储文件
        docParser.parse(docList);
        System.out.println("解析文件和存储文件");

        // 构建索引并存储
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
