package team.snof.simplesearch.search.service;

import team.snof.simplesearch.search.model.dao.doc.Doc;

import java.util.List;

public interface DocService {
    void insert(Doc doc);

    void insert(List<Doc> docs);
}
