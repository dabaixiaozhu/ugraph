package com.winit.graph.entity.inputvo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 对应 graph_app_filter_rule
 */
public class AppFilterRuleSaveVO {

    /**
     * app编号
     * app_name
     */
    @JsonProperty(value = "app_name")
    private String appName;

    /**
     * 过滤的内容
     * filter_name
     */
    @JsonProperty(value = "filter_name")
    private String filterName;

    /**
     * 过滤的类型，目前是1，表示过滤XX结尾的类
     * filter_type
     */
    @JsonProperty(value = "filter_type")
    private Integer filterType;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public Integer getFilterType() {
        return filterType;
    }

    public void setFilterType(Integer filterType) {
        this.filterType = filterType;
    }
}
