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
public class DocVO {

    @ApiModelProperty
    public Long docId;

    @ApiModelProperty
    public String url;

    @ApiModelProperty
    public String caption;

}
