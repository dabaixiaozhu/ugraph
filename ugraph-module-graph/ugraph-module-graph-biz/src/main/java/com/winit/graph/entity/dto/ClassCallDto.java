package com.winit.graph.entity.dto;

/**
 * graph_class_call
 */
public class ClassCallDto {

    /**
     * 自增的主键id
     * id
     */
    private Long id;

    /**
     * 调用类型
     * call_type 目前就父子类调用，接口及实现类调用
     */
    private String callType;

    /**
     * 调用方，类hash
     * caller_class_hash
     */
    private String callerClassHash;

    /**
     * 被调用方，类hash
     * callee_class_hash
     */
    private String calleeClassHash;

    /**
     * 被调用方，完整类名
     * callee_class_full_name
     */
    private String calleeClassFullName;

    /**
     * 被调用方，简单类名
     * callee_class_name
     */
    private String calleeClassName;

    /**
     * app唯一标识
     */
    private Integer appNum;

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public Integer getAppNum() {
        return appNum;
    }

    public void setAppNum(Integer appNum) {
        this.appNum = appNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCallerClassHash() {
        return callerClassHash;
    }

    public void setCallerClassHash(String callerClassHash) {
        this.callerClassHash = callerClassHash;
    }

    public String getCalleeClassHash() {
        return calleeClassHash;
    }

    public void setCalleeClassHash(String calleeClassHash) {
        this.calleeClassHash = calleeClassHash;
    }

    public String getCalleeClassFullName() {
        return calleeClassFullName;
    }

    public void setCalleeClassFullName(String calleeClassFullName) {
        this.calleeClassFullName = calleeClassFullName;
    }

    public String getCalleeClassName() {
        return calleeClassName;
    }

    public void setCalleeClassName(String calleeClassName) {
        this.calleeClassName = calleeClassName;
    }
}
