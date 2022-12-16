package com.winit.graph.entity.vo;

/**
 * 对应 graph_app_filter_rule
 */
public class AppFilterRuleVO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * app编号
     * app_name
     */
    private String appName;

    /**
     * 过滤的内容
     * filter_name
     */
    private String filterName;

    /**
     * 过滤的类型，目前是1，表示过滤XX结尾的类
     * filter_type
     */
    private Integer filterType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
