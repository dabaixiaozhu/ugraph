package com.winit.graph.entity.vo;

import java.util.List;

/**
 * 返回类详情
 */
public class MethodDetailVO {

    /**
     * 当前调用分所属的项目
     */
    private Integer appNum;


    /**
     * 调用节点，方法hash+字节数
     */
    private String methodHash;

    /**
     * 调用节点，方法名（简易）
     */
    private String methodName;

    /**
     *  调用节点，方法名（完整）
     */
    private String methodFullName;

    /**
     * 全类名
     */
    private String classFullName;

    /**
     * 类哈希
     */
    private String classHash;

    /**
     * 注解内容
     */
    private String annotation;

    /**
     * 0表示Trunk，1表示Version
     */
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAppNum() {
        return appNum;
    }

    public void setAppNum(Integer appNum) {
        this.appNum = appNum;
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

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
}
