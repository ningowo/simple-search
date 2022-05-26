package team.snof.simplesearch.search.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team.snof.simplesearch.search.mapper.DocMapper;
import team.snof.simplesearch.search.model.dao.doc.Doc;
import team.snof.simplesearch.search.service.DocService;


import java.util.List;

@Service
public class DocServiceImpl implements DocService {
    @Autowired
    DocMapper docMapper;

    public void insert(Doc doc) {
        docMapper.insert(doc);
    }

    public void insert(List<Doc> docs) {
        for (Doc doc : docs) {
            docMapper.insert(doc);
        }
    }
}
