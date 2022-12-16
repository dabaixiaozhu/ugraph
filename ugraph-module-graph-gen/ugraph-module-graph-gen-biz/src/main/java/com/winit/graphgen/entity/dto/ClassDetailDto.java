package com.winit.graphgen.entity.dto;

import io.swagger.models.auth.In;

/**
 * graph_class_detail
 */
public class ClassDetailDto {

    /**
     * 自增的主键id
     * id
     */
    private Integer id;

    /**
     * 类hash
     * class_hash
     */
    private String classHash;

    /**
     * 完整类名
     * class_full_name
     */
    private String classFullName;

    /**
     * 简单类名
     * class_name
     */
    private String className;

    /**
     * 注解内容，允许为空
     * annotation_text
     */
    private String annotationText;

    /**
     * 调用方，Jar包序号
     * app_num
     */
    private Integer appNum;

    public Integer getAppNum() {
        return appNum;
    }

    public void setAppNum(Integer appNum) {
        this.appNum = appNum;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getAnnotationText() {
        return annotationText;
    }

    public void setAnnotationText(String annotationText) {
        this.annotationText = annotationText;
    }
}
