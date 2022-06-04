package team.snof.simplesearch.common.util;

import org.springframework.stereotype.Component;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

@Component
public class IKAnalyzerUtil {

    public Map<String, Integer> analyze(String msg, List<String> filterWords) throws IOException {
        StringReader sr = new StringReader(msg);
        IKSegmenter ik = new IKSegmenter(sr, true);
        Lexeme lex = null;
        Set<String> filterSet = new HashSet<>(filterWords);
        Map<String, Integer> map = new HashMap<>();
        while((lex=ik.next())!=null){
            String segmentedWord = lex.getLexemeText();
            if (!filterSet.contains(segmentedWord)) {
                map.put(segmentedWord, map.getOrDefault(segmentedWord, 0) + 1);
            }
        }
        return map;
    }
}