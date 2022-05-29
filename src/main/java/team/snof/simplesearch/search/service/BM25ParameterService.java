package team.snof.simplesearch.search.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import team.snof.simplesearch.search.mapper.BM25ParameterMapper;
import team.snof.simplesearch.search.model.dao.BM25Parameter;

import java.util.HashMap;

@Slf4j
public class BM25ParameterService {
    @Autowired
    BM25ParameterMapper bm25ParameterMapper;

    public void saveParameter(String word, long doc_id, long word_freq) {
        try {
            // 存储  调用mapper
            BM25Parameter bm25Parameter = new BM25Parameter();
            bm25Parameter.setWord(word);
            bm25Parameter.setDoc_id(doc_id);
            bm25Parameter.setWord_freq(word_freq);

            bm25ParameterMapper.insertBM25Parameter(bm25Parameter);
        } catch (Exception e) {
            log.error("");
        }
    }

    public long getRecordNum() {
        return bm25ParameterMapper.getRecordNum();
    }

    public HashMap<String, Long> getWordDocNum() {
        return bm25ParameterMapper.getWordDocNum();
    }

    public BM25Parameter getRecord(Long id) {
        return bm25ParameterMapper.getRecord(id);
    }
}