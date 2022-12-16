package com.winit.graph.entity.vo;

public class AppClassRuleVO {

    private Long id;

    private String appNum;

    private String classHash;

    private String classFullName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppNum() {
        return appNum;
    }

    public void setAppNum(String appNum) {
        this.appNum = appNum;
    }

    public String getClassHash() {
        return classHash;
    }

    public void setClassHash(String classHash) {
        this.classHash = classHash;
    }

    public String getClassFullName() {
        return classFullName;
    }

    public void setClassFullName(String classFullName) {
        this.classFullName = classFullName;
    }
}
