package com.winit.graph.entity.dto;

import java.sql.Timestamp;

/**
 * 分析日志 graph_app_operate_log
 */
public class AppOperateLogDTO {

    /**
     * 自增的主键id
     */
    private Long id;

    /**
     * app名称
     * app_name
     */
    private String appName;

    /**
     * app_version
     */
    private String appVersion;

    /**
     * 生成的状态，0表示生成中，1表示已完成，2表示碰到了异常
     * status
     */
    private Integer status;

    /**
     * 日志的版本，1表示此版本最新，0表示是旧版本
     */
    private Integer version;

    /**
     * 创建时间
     * create_time
     */
    private Timestamp createTime;

    public AppOperateLogDTO(Builder builder) {
        this.appName = builder.appName;
        this.appVersion = builder.appVersion;
        this.status = builder.status;
        this.createTime = builder.createTime;
        this.version = builder.version;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

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

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public static class Builder {
        private String appName;

        private String appVersion;

        private Integer status;

        private Timestamp createTime = new Timestamp(System.currentTimeMillis());

        private Integer version;

        public Builder() {
        }

        public Builder setAppName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder setAppVersion(String appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        public Builder setStatus(Integer status) {
            this.status = status;
            return this;
        }

        public Builder setVersion(Integer version) {
            this.version = version;
            return this;
        }

        public Builder setCreateTime(Timestamp createTime) {
            this.createTime = createTime;
            return this;
        }

        public AppOperateLogDTO build() {
            return new AppOperateLogDTO(this);
        }
    }
}
