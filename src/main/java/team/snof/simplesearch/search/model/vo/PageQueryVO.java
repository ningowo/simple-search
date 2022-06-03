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

    @ApiModelProperty(value = "每页数量", required = true)
    protected Integer pageSize;

    @ApiModelProperty(value = "查询页码", required = true)
    protected Integer pageNum;

    @ApiModelProperty(value = "总请求数", notes = "请求时空着")
    protected Long total;

}