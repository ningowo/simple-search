package team.snof.simplesearch.search.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import team.snof.simplesearch.search.model.dao.Doc;

@Getter
@Setter
@ToString
@Builder
@ApiModel
public class DocVO {

    @ApiModelProperty
    public String docId;

    @ApiModelProperty
    public String url;

    @ApiModelProperty
    public String caption;

    public static DocVO buildDocVO(Doc doc) {
        return DocVO.builder()
                .docId(doc.getId())
                .url(doc.getUrl())
                .caption(doc.getCaption())
                .build();
    }
}
