package team.snof.simplesearch.search.model.dao.favorite;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * 
 *
 * @author Zhouyg
 * @date 2022-05-28
 */
@Data
@TableName("dataset")
public class Dataset {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("url")
    private String url;

    @TableField("caption")
    private String caption;

}
