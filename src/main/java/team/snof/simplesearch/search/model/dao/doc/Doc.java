package team.snof.simplesearch.search.model.dao.doc;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Doc implements Serializable {

    public Long SnowflakeDocId;

    public String url;

    public String caption;

}
