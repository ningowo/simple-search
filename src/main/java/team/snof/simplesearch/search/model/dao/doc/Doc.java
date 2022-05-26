package team.snof.simplesearch.search.model.dao.doc;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 正排索引的文档
 */

@Data
@TableName(value = "tb_doc")
public class Doc {
    @TableId(value = "id")
    private Long id;

    @TableField("url")
    private String url;

    @TableField("caption")
    private String caption;

    public Doc(String url, String caption) {
        this.url = url;
        this.caption = caption;
    }
}
