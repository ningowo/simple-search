package team.snof.simplesearch.search.model.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static team.snof.simplesearch.search.model.vo.ResultVOConstants.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class ResultVO<T> {

    @ApiModelProperty
    private int code;

    @ApiModelProperty
    private String msg;

    @ApiModelProperty
    private T data;

    public ResultVO(int code) {
        this.code = code;
    }

    public ResultVO(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public static <T> ResultVO<T> newSuccessResult() {
        return new ResultVO<>(SUCCESS);
    }

    public static <T> ResultVO<T> newSuccessResult(T data) {
        return new ResultVO<>(SUCCESS, data);
    }

    public static <T> ResultVO<String> newFailedResult(String msg) {
        return new ResultVO<>(COMMON_ERROR, "请求失败: " + msg);
    }

    public static <T> ResultVO<String> newParamErrorResult(String msg) {
        return new ResultVO<>(PARAM_ERROR, "参数错误: " + msg);
    }

    public static <T> ResultVO<String> newServerErrorResult(String msg) {
        return new ResultVO<>(SERVER_ERROR, "服务器内部错误: " + msg);
    }


}
