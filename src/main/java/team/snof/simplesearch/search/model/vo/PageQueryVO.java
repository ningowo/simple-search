package team.snof.simplesearch.search.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@ToString
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class PageQueryVO {

    @ApiModelProperty(value = "每页数量")
    protected Integer pageSize;

    @ApiModelProperty(value = "当前页码")
    protected Integer pageNum;

    @ApiModelProperty(notes = "总文档数，请求时空着")
    protected Long total;

}