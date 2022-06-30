package team.snof.simplesearch.search.model.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WordFreq {

    private String word;

    private Integer freq;

}