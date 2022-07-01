package team.snof.simplesearch.search.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.CSVFileReader;
import team.snof.simplesearch.search.engine.DocParser;
import team.snof.simplesearch.search.model.dao.Doc;

import java.util.List;

@Slf4j
@Component
public class IndexGenerateService {

    @Autowired
    DocParser docParser;

    public void generate(String path) {
        // 从csv文件获取Doc
        log.info("开始读取文件...");
        List<Doc> docList = CSVFileReader.readFile(path);
        log.info("读取文件完成，文件数量：" + docList.size());

        // 解析文件和存储文件
        log.info("开始解析文件并构建索引...");
        long startTime = System.currentTimeMillis();
        long parseDocNum = docParser.parseAndBuild(docList);
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        log.info("解析完成！用时：{}ms {}s, 总文档数：{}", time, time / 1000, parseDocNum);
    }

}
