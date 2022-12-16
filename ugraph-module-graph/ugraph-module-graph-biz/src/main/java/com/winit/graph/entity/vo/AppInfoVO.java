package com.winit.graph.entity.vo;

public class AppInfoVO {

    /**
     * 主键id
     */
    private Long id;

    /**
     *
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
