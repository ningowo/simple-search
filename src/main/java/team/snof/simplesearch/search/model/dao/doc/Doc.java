package team.snof.simplesearch.search.model.dao.doc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doc {

    public Long SnowflakeDocId;

    public String url;

    public String caption;

    public Doc(String url, String caption) {
        this.url = url;
        this.caption = caption;
    }
}
