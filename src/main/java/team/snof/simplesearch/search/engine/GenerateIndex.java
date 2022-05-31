package team.snof.simplesearch.search.engine;

import team.snof.simplesearch.common.util.CSVFileReader;
import team.snof.simplesearch.search.model.dao.Doc;
import java.util.List;

public class GenerateIndex {

    public static void main(String[] args) {
//        TODO 大数据量的分批操作和多线程操作
        // 从csv文件获取Doc
        List<Doc> docList = CSVFileReader.readDocsFromCSV("");

        // 解析文件和存储文件
        DocParser docParser = new DocParser();
        docParser.parse(docList);

        // 构建索引并存储
        IndexBuilder indexBuilder = new IndexBuilder();
        indexBuilder.buildIndexes();
    }
}
