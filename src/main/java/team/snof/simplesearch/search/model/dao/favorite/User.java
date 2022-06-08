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
@TableName("user")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("enabled")
    private Boolean enabled;

    @TableField("accountNonExpired")
    private Boolean accountnonexpired;

    @TableField("accountNonLocked")
    private Boolean accountnonlocked;

    @TableField("credentialsNonExpired")
    private Boolean credentialsnonexpired;

}
