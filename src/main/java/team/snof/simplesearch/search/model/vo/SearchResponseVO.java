package team.snof.simplesearch.search.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@ApiModel("搜索结果列表")
public class SearchResponseVO {

    @ApiModelProperty(value = "文档列表")
    private List<DocVO> docVOList;

    @ApiModelProperty(value = "相关搜索列表")
    private List<String> relatedSearchList;

    @ApiModelProperty(value = "分页信息")
    private SearchRequestVO query;

}
