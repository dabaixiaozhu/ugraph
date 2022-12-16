package com.winit.graph.entity.inputvo;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class MethodDownRelationInputVO {

    /**
     * app的唯一标识
     */
    @JsonProperty(value = "app_name")
    @NotEmpty(message = "app_name缺失")
    private String appName;

    /**
     * app版本
     */
    @JsonProperty(value = "app_version")
    @NotEmpty(message = "app_version缺失")
    private String appVersion;

    /**
     * 方法哈希，确定唯一变量
     */
    @JsonProperty(value = "method_hash")
    @NotEmpty(message = "method_hash缺失")
    private String methodHash;

    /**
     * 递归层数
     */
    @JsonProperty(value = "depth")
    @NotNull(message = "depth缺失")
    private Integer depth;

    /**
     * 0表示不使用过滤规则，1表示使用过滤规则
     */
    @JsonProperty(value = "show_all")
    @NotNull(message = "show_all缺失")
    private Integer showAll;

    /**
     * 0表示TRUNK 1表示VERSION
     */
    @JsonProperty(value = "app_type")
    @NotNull(message = "app_type缺失")
    private Integer appType;

    /**
     * 0表示不在APP 1表示在APP
     */
    @JsonProperty(value = "in_app")
    @NotNull(message = "in_app缺失")
    private Integer InApp;

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getInApp() {
        return InApp;
    }

    public void setInApp(Integer inApp) {
        InApp = inApp;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getShowAll() {
        return showAll;
    }

    public void setShowAll(Integer showAll) {
        this.showAll = showAll;
    }

    public String getMethodHash() {
        return methodHash;
    }

    public void setMethodHash(String methodHash) {
        this.methodHash = methodHash;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    @Override
    public String toString() {
        return "MethodDownRelationInputVO{" +
                "appName='" + appName + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", methodHash='" + methodHash + '\'' +
                ", depth=" + depth +
                ", showAll=" + showAll +
                ", appType=" + appType +
                ", InApp=" + InApp +
                '}';
    }
}
