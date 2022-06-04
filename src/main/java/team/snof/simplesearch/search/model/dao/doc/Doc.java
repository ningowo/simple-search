package team.snof.simplesearch.search.model.dao.doc;

import lombok.Data;

@Data
public class Doc {

    private Long SnowflakeDocId;

    private String url;

    private String caption;

    public Doc(Long snowflakeDocId, String url, String caption) {
        SnowflakeDocId = snowflakeDocId;
        this.url = url;
        this.caption = caption;
    }

    public Long getSnowflakeDocId() {
        return SnowflakeDocId;
    }

    public void setSnowflakeDocId(Long snowflakeDocId) {
        SnowflakeDocId = snowflakeDocId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
