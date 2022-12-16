package com.winit.graph.entity.vo;

import com.winit.graph.common.exception.enums.GlobalErrorCodeConstants;

import java.io.Serializable;

/**
 * 通用返回
 */
public class CommonResultVO implements Serializable {

    /**
     * 错误码
     */
    private Integer status;

    /**
     * 返回数据
     */
    private Object data;

    /**
     * 错误提示，用户可阅读
     */
    private String msg;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
