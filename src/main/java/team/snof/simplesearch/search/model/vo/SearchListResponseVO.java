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
public class SearchListResponseVO {

    @ApiModelProperty("doc list")
    private List<DocVO> docVOList;

    @ApiModelProperty("request")
    private SearchRequestVO request;

}
