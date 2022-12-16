package com.winit.graph.entity.dto;

/**
 * graph_class_detail
 */
public class ClassDetailDTO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * class_hash
     * 类哈希值
     */
    private String classHash;

    /**
     * class_full_name
     * 类的全路径
     */
    private String classFullName;

    /**
     * class_name
     * 简单类名
     */
    private String className;

    /**
     * annotation_text
     * 类注解内容
     */
    private String annotation;

    /**
     * app_num
     * 类所属的应用
     */
    private Integer appNum;

    /**
     * type
     * 0表示Trunk，1表示Version
     */
    private Integer status = 0;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public Integer getAppNum() {
        return appNum;
    }

    public void setAppNum(Integer appNum) {
        this.appNum = appNum;
    }
}
