package team.snof.simplesearch.search.model.dao.favorite;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("favourite")
public class Favourite {

    @TableId(value = "id",  type = IdType.AUTO)
    private Integer id;

    @TableField("user_id")
    private Integer userId;

    @TableField("favourite_name")
    private String favouriteName;

    public Favourite(Integer userId, String favouriteName) {
        this.userId = userId;
        this.favouriteName = favouriteName;
    }
}