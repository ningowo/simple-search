package team.snof.simplesearch.search.service;

import team.snof.simplesearch.search.model.dao.DocInfo;
import team.snof.simplesearch.search.model.dao.Index;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


public class SortService {
    private static final double k_3 = 1.5;  // k3  1.2~2
    public static List<Long> order(List<String> query, List<Index> indexs) {
        //1.计算分词在query出的出现次数
        HashMap<String, Long> tf = new HashMap<>();
        for (String word : query) {
            tf.put(word, tf.getOrDefault(word, 0L) + 1);
        }

        //2.计算文档对应的相似度
        HashMap<Long, BigDecimal> docSimilarity = new HashMap<>();
        for (Index index : indexs) {
            String word = index.getIndexKey();
            for (DocInfo doc : index.getDocInfoList()) {
                BigDecimal corr = doc.getCorr().multiply(new BigDecimal((k_3 + 1) * tf.get(word)))
                        .divide(new BigDecimal(k_3 + tf.get(word)));
                docSimilarity.put(doc.getDocId(), docSimilarity.getOrDefault(doc.getDocId(), new BigDecimal(0)).add(corr));
            }

        }

        //3.按相似度从高到低排序
        TreeMap<BigDecimal, Long> docs = new TreeMap<>();
        for (Map.Entry<Long, BigDecimal> entry : docSimilarity.entrySet()) {
            docs.put(entry.getValue(), entry.getKey());
        }
        docs.descendingMap();

        List<Long> resultDocs = new ArrayList<>();
        Iterator it = docs.keySet().iterator();
        while (it.hasNext()) {
            BigDecimal key = (BigDecimal) it.next();
            resultDocs.add(docs.get(key));
        }
        return resultDocs;
    }

    private static void test(){
        List<String> query = new ArrayList<>(2);
        List<Index> indexs = new ArrayList<>(2);
        query.add("test");query.add("namo");

        List<DocInfo>  index1 = new ArrayList<>();
        List<DocInfo>  index2 = new ArrayList<>();
        index1.add(new DocInfo(1,3,new BigDecimal(0.5)));
        index1.add(new DocInfo(2,4,new BigDecimal(0.6)));
        index2.add(new DocInfo(1,3,new BigDecimal(0.4)));
        index2.add(new DocInfo(2,4,new BigDecimal(0.3)));

        indexs.add(new Index("test",index1));  indexs.add(new Index("namo",index2));

        List<Long> docs = order(query,indexs);
        for(Long docID:docs){
            System.out.printf("%d\n",docID);
        }
    }
}
