package team.snof.simplesearch.user.model.bo.favorite;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 *
 * @author Zhouyg
 * @date 2022-05-28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("collection")
public class Collection {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("favourite_id")
    private Integer favouriteId;

    @TableField("data_id")
    private String dataId;

    public Collection(Integer favouriteId, String dataId) {
        this.favouriteId = favouriteId;
        this.dataId = dataId;
    }
}
