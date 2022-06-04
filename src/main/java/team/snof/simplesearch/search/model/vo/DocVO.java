package team.snof.simplesearch.search.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import team.snof.simplesearch.search.model.dao.doc.Doc;

@Getter
@Setter
@ToString
@Builder
@ApiModel
public class DocVO {

    @ApiModelProperty
    public Long docId;

    @ApiModelProperty
    public String url;

    @ApiModelProperty
    public String caption;

    public static DocVO buildDocVO(Doc doc) {
        return DocVO.builder()
                .docId(doc.getSnowflakeDocId())
                .url(doc.getUrl())
                .caption(doc.getCaption())
                .build();
    }
}
