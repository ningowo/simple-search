package team.snof.simplesearch.search.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@ApiModel("搜索结果列表")
public class SearchResponseVO {

    @ApiModelProperty(value = "文档列表")
    private List<DocVO> docVOList;

    @ApiModelProperty(value = "文档列表ID")
    private List<Long> DocIds;

    @ApiModelProperty(value = "相关搜索列表")
    private List<String> relatedSearchList;

    @ApiModelProperty(notes = "看周寅刚的实现方式是否要用到这个")
    private SearchRequestVO request;

}
