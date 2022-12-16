package com.winit.graph.entity.inputvo;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public class AppClassRuleInputVO {

    @JsonProperty(value = "app_name")
    @NotEmpty(message = "app_name缺失")
    private String appName;

    @JsonProperty(value = "full_class_name")
    @NotEmpty(message = "full_class_name缺失")
    private String fullClassName;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
    }
}
