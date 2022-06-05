package team.snof.simplesearch.search.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.snof.simplesearch.common.util.IKAnalyzerUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhouyg
 * @date 2022/6/5
 */

@SpringBootTest
public class QueryAnalyzerTest {

    @Autowired
    private IKAnalyzerUtil ikAnalyzerUtil;

    @Test
    public void queryAnaLyzerTest() {
        String query = "字节跳动，中华人民共和国，字节跳动加班猝死";
        List<String> list = new ArrayList<>();
        list.add("加班");
        list.add("猝死");
        Map<String, Integer> analyzer = null;
        try {
            analyzer = ikAnalyzerUtil.analyze(query, list);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println(analyzer);
        }
    }
}
