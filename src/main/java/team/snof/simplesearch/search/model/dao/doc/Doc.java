package team.snof.simplesearch.search.model.dao.doc;


import lombok.Data;

import java.io.Serializable;

import java.io.Serializable;

@Data
public class Doc implements Serializable {

    public Long SnowflakeDocId;

    public String url;

    public String caption;

}
