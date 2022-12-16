package com.winit.graph.common.exception.util;


import com.winit.graph.common.exception.ErrorCode;
import com.winit.graph.common.exception.ServiceException;

/**
 * 工具类
 * 目的在于，格式化异常信息提示。
 */
public class ServiceExceptionUtil {

    /**
     * 根据传入的 ErrorCode 来封装统一的返回内容
     */
    public static ServiceException exception(ErrorCode errorCode) {
        return new ServiceException(errorCode.getCode(), errorCode.getMsg());
    }
}
