package com.winit.graphgen.entity.dto;

/**
 * graph_method_call
 */
public class MethodDetailDto {

    /**
     * 序号
     * id
     */
    private Integer id;

    /**
     * 方法hash
     * method_hash
     */
    private String methodHash;

    /**
     * 简单的方法名
     * method_name
     */
    private String methodName;

    /**
     * 完整方法（方法名+参数）
     * method_full_name
     */
    private String methodFullName;

    /**
     * 完整类名
     * class_full_name
     */
    private String classFullName;

    /**
     * 类hash
     * class_hash
     */
    private String classHash;

    /**
     * 注解内容
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

    public String getMethodHash() {
        return methodHash;
    }

    public void setMethodHash(String methodHash) {
        this.methodHash = methodHash;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodFullName() {
        return methodFullName;
    }

    public void setMethodFullName(String methodFullName) {
        this.methodFullName = methodFullName;
    }

    public String getClassFullName() {
        return classFullName;
    }

    public void setClassFullName(String classFullName) {
        this.classFullName = classFullName;
    }

    public String getClassHash() {
        return classHash;
    }

    public void setClassHash(String classHash) {
        this.classHash = classHash;
    }

    public String getAnnotationText() {
        return annotationText;
    }

    public void setAnnotationText(String annotationText) {
        this.annotationText = annotationText;
    }
}
