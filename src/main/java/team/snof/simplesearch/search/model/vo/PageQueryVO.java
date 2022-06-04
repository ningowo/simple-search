package team.snof.simplesearch.search.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ApiModel
public class PageQueryVO {

    @ApiModelProperty
    protected Integer pageSize;

    @ApiModelProperty
    protected Integer pageNum;

    @ApiModelProperty(notes = "请求时空着")
    protected Long total;

}