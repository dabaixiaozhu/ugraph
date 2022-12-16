package com.winit.graph.entity.inputvo;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class MethodUpRelationInputVO {

    /**
     * app的唯一标识
     */
    @JsonProperty(value = "app_name")
    @NotEmpty(message = "app_name缺失")
    private String appName;

    /**
     * 方法哈希，确定唯一变量
     */
    @JsonProperty(value = "method_hash")
    @NotEmpty(message = "method_hash缺失")
    private String methodHash;

    /**
     * 0表示不使用过滤规则，1表示使用过滤规则
     */
    @JsonProperty(value = "show_all")
    @NotNull(message = "show_all缺失")
    private Integer showAll;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getMethodHash() {
        return methodHash;
    }

    public void setMethodHash(String methodHash) {
        this.methodHash = methodHash;
    }

    public Integer getShowAll() {
        return showAll;
    }

    public void setShowAll(Integer showAll) {
        this.showAll = showAll;
    }
}
