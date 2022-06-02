package team.snof.simplesearch.search.service;

import team.snof.simplesearch.search.model.dao.DocInfo;
import team.snof.simplesearch.search.model.dao.Index;

import java.math.BigDecimal;
import java.util.*;


public class SortService {
    private static final double k_3 = 1.5;  // k3  1.2~2

    // 文档类用于排序，{docID,similarity}
    private static class Doc implements  Comparable<Doc> {
        private Long DocId;
        private BigDecimal similarity;
        public Doc(Long docId,BigDecimal similarity){
            this.DocId = docId;
            this.similarity = similarity;
        }
        public int compareTo(Doc y){
            return similarity.compareTo(y.similarity);
        }
    }

    public static List<Long> order(List<String> words, List<Index> indexs) {
        //1.计算分词在query出的出现次数
        HashMap<String, Long> word2TermFrequency = new HashMap<>();// kv <word,term frequency>
        for (String word : words) {
            word2TermFrequency.put(word, word2TermFrequency.getOrDefault(word, 0L) + 1);
        }

        //2.计算文档对应的相似度
        HashMap<Long, BigDecimal> doc2Similarity = new HashMap<>();//kv <docID,similarity>
        for (Index index : indexs) {
            String word = index.getIndexKey();
            for (DocInfo doc : index.getDocInfoList()) {
                BigDecimal corr = doc.getCorr().multiply(new BigDecimal((k_3 + 1) * word2TermFrequency.get(word)))
                        .divide(new BigDecimal(k_3 + word2TermFrequency.get(word)));
                //System.out.println(corr.toString());
                doc2Similarity.put(doc.getDocId(), doc2Similarity.getOrDefault(doc.getDocId(), new BigDecimal(0)).add(corr));
            }

        }

        //3.按相似度从高到低排序
        Doc [] docs = new Doc[doc2Similarity.size()];
        int idx = 0;
        for(Map.Entry<Long,BigDecimal> entry:doc2Similarity.entrySet()){
            docs[idx++] = new Doc(entry.getKey(),entry.getValue());
        }
        List<Long> orderedDocs = new ArrayList<>();//DocId
        Arrays.sort(docs,Collections.reverseOrder());

        for(Doc doc:docs){
            orderedDocs.add(doc.DocId);
            //System.out.printf("%d %f\n",doc.DocId,Double.valueOf(doc.similarity.toString()));
        }
        return orderedDocs;
    }
    private static void test(){
        List<String> query = new ArrayList<>(2);
        List<Index> indexs = new ArrayList<>(2);
        query.add("test");query.add("namo");

        List<DocInfo>  index1 = new ArrayList<>();
        List<DocInfo>  index2 = new ArrayList<>();
        index1.add(new DocInfo(1,3,new BigDecimal(0.2)));
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
