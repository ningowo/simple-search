package team.snof.simplesearch.search.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.CSVFileReader;
import team.snof.simplesearch.search.model.dao.doc.Doc;

import java.util.ArrayList;
import java.util.List;

@Component
public class IndexGenerateRunner {
    @Autowired
    DocParser docParser;
    @Autowired
    IndexBuilder indexBuilder;

    public void generate() throws Exception {
        // 获取地址列表
//        String root = "D:/GoCamp/wukong_release/";
//        List<String> filePathList = new ArrayList<String>(Collections.singletonList(""));

//        File file = new File(root);
//        List<String> filePathList = Arrays.asList(file.list());
        List<String> filePathList = new ArrayList<>();
        filePathList.add("D:/GoCamp/wukong50k_release.csv");

        // 从csv文件获取Doc
        for (String fileName: filePathList) {
//            String filePath = root + fileName;
            String filePath = fileName;

            List<Doc> docList = CSVFileReader.readFile(filePath);

            // 解析文件和存储文件
//            DocParser docParser = new DocParser();
            docParser.parse(docList);
            System.out.println("解析文件和存储文件");
        }

        // 构建索引并存储
//        IndexBuilder indexBuilder = new IndexBuilder();
        indexBuilder.buildIndexes();
        System.out.println("构建索引并存储");
    }
}
