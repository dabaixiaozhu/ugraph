package com.winit.graph.entity.inputvo;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class GraphLogInputVO {

    /**
     * 当前页
     */
    @JsonProperty(value = "page_num")
    @NotNull(message = "page_num缺失")
    private Integer pageNum;

    /**
     * 一页显示的条数
     */
    @JsonProperty(value = "page_size")
    @NotNull(message = "page_size缺失")
    private Integer pageSize;

    /**
     * 日志的版本，1表示新版本，0表示旧版本，什么都不传表示全部查询
     */
    @JsonProperty(value = "version")
    private Integer version;

    @JsonProperty(value = "app_name")
    private String appName;

    @JsonProperty(value = "app_version")
    private String appVersion;

    /**
     * 生成的状态，0表示生成中，1表示已完成，2表示碰到了异常
     */
    @JsonProperty(value = "status")
    private Integer status;

    /**
     * 开始时间
     */
    @JsonProperty(value = "start_time")
    private String startTime;

    /**
     * 结束时间
     */
    @JsonProperty(value = "end_time")
    private String endTime;

    /**
     * DESC ASC，默认是DESC
     */
    @JsonProperty(value = "show_type")
    private String showType = "DESC";

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }
}
