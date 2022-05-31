package team.snof.simplesearch.search.model.bo.favorite;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import team.snof.simplesearch.search.model.bo.favorite.audit.DateAudit;

import javax.persistence.*;

/**
 * @author Zhouyg
 * @date 2022-05-28
 */
@Data
@Entity
@TableName("user")
public class User extends DateAudit {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("email")
    private String email;

    @TableField("enabled")
    private Boolean enabled;

    @TableField("accountNonExpired")
    private Boolean accountnonexpired;

    @TableField("accountNonLocked")
    private Boolean accountnonlocked;

    @TableField("credentialsNonExpired")
    private Boolean credentialsnonexpired;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

}
