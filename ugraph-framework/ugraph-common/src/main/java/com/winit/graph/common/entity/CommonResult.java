package com.winit.graph.common.entity;

import com.winit.graph.common.exception.ErrorCode;
import com.winit.graph.common.exception.enums.GlobalErrorCodeConstants;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * 通用返回
 *
 * @param <T> 数据泛型
 */
public class CommonResult<T> implements Serializable {

    /**
     * 错误码
     */
    private Integer status;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 错误提示，用户可阅读
     */
    private String msg;

    public static <T> CommonResult<T> success(T data, String msg) {
        CommonResult<T> result = new CommonResult<>();
        result.status = GlobalErrorCodeConstants.SUCCESS.getCode();
        result.data = data;
        result.msg = msg;
        return result;
    }

    /**
     * 根据错误码和错误信息，封装返回信息
     *
     * @param code    错误码
     * @param message 错误信息
     * @return 全局统一的返回信息
     */
    public static <T> CommonResult<T> error(Integer code, String message) {
        Assert.isTrue(!GlobalErrorCodeConstants.SUCCESS.getCode().equals(code), "code 必须是错误的！");
        CommonResult<T> result = new CommonResult<>();
        result.status = code;
        result.msg = message;
        return result;
    }

    // ————————————————成功相关————————————————

    /**
     * 还是调用上面的方法
     */
    public static <T> CommonResult<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMsg());
    }

    public Integer getStatus() {
        return status;
    }

    // ————————————————错误相关，和 Exception 异常体系集成————————————————

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }
}
