package team.snof.simplesearch.search.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.CSVFileReader;
import team.snof.simplesearch.search.engine.index.DocParser;
import team.snof.simplesearch.search.engine.index.IndexBuilder;
import team.snof.simplesearch.search.model.dao.doc.Doc;

import java.util.List;

@Slf4j
@Component
public class IndexGenerateService {

    @Autowired
    DocParser docParser;

    @Autowired
    IndexBuilder indexBuilder;

    public void generate(String path) {
        // 从csv文件获取Doc
        log.info("开始读取文件...");
        List<Doc> docList = CSVFileReader.readFile(path);
        log.info("读取文件完成，文件数量：" + docList.size());

        // 解析文件和存储文件
        log.info("开始解析文件...");
        docParser.parse(docList);
        log.info("解析完成！");

        log.info("开始构建索引文件...");
        indexBuilder.buildIndexes();
        log.info("索引构建完成！");
    }

    public void parseAndStoreDocs(String path) {
        // 从csv文件获取Doc
        log.info("开始读取文件...");
        List<Doc> docList = CSVFileReader.readFile(path);
        log.info("读取文件完成，文件数量={}", docList.size());

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

}
