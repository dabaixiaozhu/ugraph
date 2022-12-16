package com.winit.graph.entity.dto;

public class AppInfoDTO {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * app_version
     */
    private String appVersion;

    /**
     * app_name
     */
    private String appName;

    /**
     * app_type
     */
    private String appType;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }
}
