package team.snof.simplesearch.search.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@ApiModel
@AllArgsConstructor
public class SearchRequestVO extends PageQueryVO {

    @ApiModelProperty
    String query;

    @ApiModelProperty
    List<String> filterWordList;

}
