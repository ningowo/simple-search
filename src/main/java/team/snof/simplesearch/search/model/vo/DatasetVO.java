package team.snof.simplesearch.search.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import team.snof.simplesearch.user.model.bo.favorite.Dataset;

/**
 * 
 *
 * @author Zhouyg
 * @date 2022-05-28
 */
@Getter
@Setter
@ToString
@Builder
@ApiModel
public class DatasetVO {

    @ApiModelProperty
    private Integer id;

    @ApiModelProperty
    private String url;

    @ApiModelProperty
    private String caption;

    public static DatasetVO buildDatasetVO(Dataset dataset) {
        return DatasetVO.builder()
                .id(dataset.getId())
                .url(dataset.getUrl())
                .caption(dataset.getCaption())
                .build();
    }

}
