package team.snof.simplesearch.search.service;

import lombok.extern.slf4j.Slf4j;
import team.snof.simplesearch.search.mapper.WordDocCorrMapper;
import team.snof.simplesearch.search.model.dao.BM25Parameter;
import team.snof.simplesearch.search.model.dao.WordDocCorr;

import java.math.BigDecimal;

@Slf4j
public class WordDocCorrService {

    public void saveWordDocCorr(String word, long doc_id, BigDecimal corr) {
        try {
            WordDocCorr wordDocCorr = new WordDocCorr(word, doc_id, corr);
            WordDocCorrMapper.insertWordDocCorr(wordDocCorr);
        } catch (Exception e) {
            log.error("");
        }
    }

    public void sortWordDocCorr() {
        try {
            WordDocCorrMapper.sortWordDocCorr();
        } catch (Exception e) {
            log.error("");
        }
    }
}
