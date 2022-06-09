package team.snof.simplesearch.search.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Getter
@Setter
@ToString
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class PageQueryVO {

    @Min(value = 1, message = "每页数量不能小于0")
    @ApiModelProperty(value = "每页数量")
    protected Integer pageSize;

    @Min(value = 1, message = "页码不能小于0")
    @ApiModelProperty(value = "当前页码")
    protected Integer pageNum;

    @ApiModelProperty(notes = "总文档数，请求时空着")
    protected Long total;

}