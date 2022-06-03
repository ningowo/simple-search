package team.snof.simplesearch.search.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@Setter
@ToString
@ApiModel
public class SearchRequestVO extends PageQueryVO {

    @ApiModelProperty(value = "要搜索的字符串")
    String query;

    @ApiModelProperty(value = "过滤词")
    List<String> filterWordList;

}
