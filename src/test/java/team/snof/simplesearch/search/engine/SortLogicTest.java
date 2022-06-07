package team.snof.simplesearch.search.engine;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import team.snof.simplesearch.search.model.dao.doc.DocInfo;
import team.snof.simplesearch.search.model.dao.index.Index;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SortLogicTest {

//    @Test
//    void sortDoc(){
//        HashMap<String, Integer> wordToFreq = new HashMap<>();
//        List<Index> indexs = new ArrayList<>(2);
//
//        wordToFreq.put("test",1);
//        wordToFreq.put("namomo",2);
//        List<DocInfo>  index1 = new ArrayList<>();
//        List<DocInfo>  index2 = new ArrayList<>();
//        index1.add(new DocInfo(1l,new BigDecimal(0.2)));
//        index1.add(new DocInfo(2l,new BigDecimal(0.6)));
//        index2.add(new DocInfo(1l,new BigDecimal(0.4)));
//        index2.add(new DocInfo(2l,new BigDecimal(0.3)));
//
//        indexs.add(new Index("test",index1));
//        indexs.add(new Index("namo",index2));
//
//        List<Long> docs = SortLogic.docSort(indexs,wordToFreq);
//        for(Long docID:docs){
//            System.out.printf("%d\n",docID);
//        }
//    }
}