package com.winit.graphgen.entity.dto;

/**
 * 存入 graph_app_info
 */
public class AppInfoDto {

    /**
     * app/Jar包的序号
     * id
     */
    private Integer id;

    /**
     * app名称
     * app_name
     */
    private String appName;

    /**
     * app的版本号
     * app_version
     */
    private String appVersion;

    /**
     * 包的类型，比如jar、war
     * app_type
     */
    private String appType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }
}
