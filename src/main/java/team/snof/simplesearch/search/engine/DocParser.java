package team.snof.simplesearch.search.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.snof.simplesearch.common.util.WordSegmentation;
import team.snof.simplesearch.search.storage.OssStorage;
import team.snof.simplesearch.common.util.SnowFlakeIDGenerator;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.model.dao.doc.DocLen;
import team.snof.simplesearch.search.model.dao.index.IndexPartial;
import team.snof.simplesearch.search.model.dao.index.TempData;
import team.snof.simplesearch.search.storage.DocLenStorage;
import team.snof.simplesearch.search.storage.IndexPartialStorage;

import java.util.*;

@Slf4j
@Component
public class DocParser {

    @Autowired
    WordSegmentation wordSegmentation;

    @Autowired
    IndexPartialStorage indexPartialStorage;

    @Autowired
    DocLenStorage docLenStorage;

    // 解析doc，并获得索引所需参数
    public void parse(List<Doc> docList) throws Exception {
        for (Doc doc : docList) {
            long docId = SnowFlakeIDGenerator.generateSnowFlakeId();  // 调用雪花id生成doc_id 后面文档存储也要用这个id
            parseDoc(doc.getUrl(), doc.getCaption(), docId);
        }
    }

    // 解析每个doc中间参数存入word_temp表中
    public void parseDoc(String url, String caption, long docId) throws Exception {
        /**
         * 分词接口返回的是map <word, word_freq>
         */
        // 分词在文档中词频
        Map<String, Integer> wordToFreqMap = wordSegmentation.segment(caption);

//      文档长度
        long docLength = wordToFreqMap.size();
        // 储存文档长度到doc_length表  {doc_id, doc_len}
        DocLen docLen = new DocLen(docId, docLength);
        docLenStorage.save(docLen);

        // 储存分词对应的文档和词频  <word, word_freq>
        for (Map.Entry<String, Integer> entry : wordToFreqMap.entrySet()) {
            TempData tempData = new TempData(docId, entry.getValue());
            String word = entry.getKey();
            List<TempData> tempDataList = new ArrayList<>();
            tempDataList.add(tempData);
            IndexPartial indexPartial = new IndexPartial(word, tempDataList);
            indexPartialStorage.saveIndexPartial(indexPartial);
        }

        // 储存文档
        Doc doc = new Doc(docId, url, caption);
        OssStorage.addDoc(doc);
    }
}
