package team.snof.simplesearch.user.model.bo.favorite;

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
@TableName("collection")
public class Collection {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("favourite_id")
    private Integer favouriteId;

    @TableField("data_id")
    private Integer dataId;

    public Collection(Integer favouriteId, Integer dataId) {
        this.favouriteId = favouriteId;
        this.dataId = dataId;
    }
}
