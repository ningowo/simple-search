package team.snof.simplesearch.search.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import team.snof.simplesearch.user.model.bo.favorite.Favourite;

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
public class FavouriteVO {

    @ApiModelProperty
    public Integer id;

    @ApiModelProperty
    public String favouriteName;

    @ApiModelProperty
    public Integer userId;

    public static FavouriteVO buildFavouriteVO(Favourite favourite) {
        return FavouriteVO.builder()
                .id(favourite.getId())
                .userId(favourite.getUserId())
                .favouriteName(favourite.getFavouriteName())
                .build();
    }
}
