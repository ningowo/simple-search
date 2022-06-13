package team.snof.simplesearch.search.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 
 *
 * @author Zhouyg
 * @date 2022-05-28
 */
@Getter
@Setter
@ToString
@ApiModel
public class CollectionVO {

    @ApiModelProperty
    private Integer id;

    @ApiModelProperty
    private Integer favouriteId;

    @ApiModelProperty
    private String dataId;

    public CollectionVO(Integer favouriteId, String dataId) {
        this.favouriteId = favouriteId;
        this.dataId = dataId;
    }
}
