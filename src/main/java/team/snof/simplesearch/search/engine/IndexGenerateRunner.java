package team.snof.simplesearch.search.engine;

import team.snof.simplesearch.common.util.CSVFileReader;
import team.snof.simplesearch.search.model.dao.doc.Doc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IndexGenerateRunner {

    public static void main(String[] args) {

        // 获取地址列表
        String root = "";
        List<String> filePathList = new ArrayList<String>(Collections.singletonList(""));

        // 从csv文件获取Doc
        for (String filePath: filePathList) {
            List<Doc> docList = CSVFileReader.readFile(filePath);

            // 解析文件和存储文件
            DocParser docParser = new DocParser();
            docParser.parse(docList);
        }

        // 构建索引并存储
        IndexBuilder indexBuilder = new IndexBuilder();
        indexBuilder.buildIndexes();
    }
}
