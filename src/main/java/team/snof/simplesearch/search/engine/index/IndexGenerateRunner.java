package team.snof.simplesearch.search.engine.index;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.CSVFileReader;
import team.snof.simplesearch.search.model.dao.doc.Doc;

import java.util.List;

@Slf4j
@Component
public class IndexGenerateRunner {

    @Autowired
    DocParser docParser;

    @Autowired
    IndexBuilder indexBuilder;

    public void generate(String path) throws Exception {

        // 从csv文件获取Doc
        List<Doc> docList = CSVFileReader.readFile(path);

        // 解析文件和存储文件
        docParser.parse(docList);
        log.info("读取文件");
        System.out.println("解析文件和存储文件");

        // 构建索引并存储
        indexBuilder.buildIndexes();
        System.out.println("构建索引并存储");
    }

    public void parseAndStoreDocs(String path) throws Exception {
        // 从csv文件获取Doc
        log.info("开始读取文件...");
        List<Doc> docList = CSVFileReader.readFile(path);
        log.info("读取文件完成，文件数量：" + docList.size());

        // 解析文件和存储文件
        log.info("开始解析文件...");
        docParser.parse(docList);
        log.info("解析完成！");
    }

    public void buildIndex() {
        log.info("开始构建索引文件...");
        indexBuilder.buildIndexes();
        log.info("索引构建完成！");
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
