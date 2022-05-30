package team.snof.simplesearch.common.util;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IKAnalyzerUtil {

    public static List<String> analyze(String msg, String... arg) throws IOException {
        StringReader sr=new StringReader(msg);
        IKSegmenter ik=new IKSegmenter(sr, true);
        Lexeme lex=null;
        Set<String> set = new HashSet<>();
        for (String s : arg) {
            set.add(s);
        }
        List<String> list=new ArrayList<>();
        while((lex=ik.next())!=null){
            String s = lex.getLexemeText();
            if (!set.contains(s)) list.add(lex.getLexemeText());
        }
        return list;
    }
    
    public static void main(String[] args) throws IOException {
        List<String> list = IKAnalyzerUtil.analyze("字节跳动中华人民共和国合同法");
        System.out.println(list);
    }
}