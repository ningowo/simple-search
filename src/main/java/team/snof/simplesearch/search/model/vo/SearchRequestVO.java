package team.snof.simplesearch.search.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@ApiModel
@AllArgsConstructor
public class SearchRequestVO extends PageQueryVO {

    @ApiModelProperty
    @NotNull(message = "查询参数不能为空")
    String query;

    @ApiModelProperty
    List<String> filterWordList;

}
