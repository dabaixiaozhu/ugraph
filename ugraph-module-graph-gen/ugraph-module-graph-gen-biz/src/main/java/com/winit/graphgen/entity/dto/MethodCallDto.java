package com.winit.graphgen.entity.dto;


/**
 * 存入 graph_method_call
 */
public class MethodCallDto {

    /**
     * 调用类型
     * call_type
     */
    private String callType;

    /**
     * 调用方，方法hash
     * caller_method_hash
     */
    private String callerMethodHash;

    /**
     * 调用方，源代码行号
     * caller_method_line_num
     */
    private Integer callerMethodLineNum;

    /**
     * 被调用方，方法hash
     * callee_method_hash
     */
    private String calleeMethodHash;

    /**
     * app的唯一标识
     * app_num
     */
    private Integer appNum;

    /**
     * 被调用方，简单的方法名
     * method_name
     */
    private String methodName;

    /**
     * 被调用方，完整方法（类名+方法名+参数）
     * method_full_name
     */
    private String methodFullName;

    /**
     * 被调用方，类哈希
     * class_hash
     */
    private String classHash;

    private String classFullName;

    public String getClassFullName() {
        return classFullName;
    }

    public void setClassFullName(String classFullName) {
        this.classFullName = classFullName;
    }

    public String getMethodFullName() {
        return methodFullName;
    }

    public void setMethodFullName(String methodFullName) {
        this.methodFullName = methodFullName;
    }

    public String getClassHash() {
        return classHash;
    }

    public void setClassHash(String classHash) {
        this.classHash = classHash;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Integer getAppNum() {
        return appNum;
    }

    public void setAppNum(Integer appNum) {
        this.appNum = appNum;
    }

    public String getCalleeMethodHash() {
        return calleeMethodHash;
    }

    public void setCalleeMethodHash(String calleeMethodHash) {
        this.calleeMethodHash = calleeMethodHash;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallerMethodHash() {
        return callerMethodHash;
    }

    public void setCallerMethodHash(String callerMethodHash) {
        this.callerMethodHash = callerMethodHash;
    }

    public Integer getCallerMethodLineNum() {
        return callerMethodLineNum;
    }

    public void setCallerMethodLineNum(Integer callerMethodLineNum) {
        this.callerMethodLineNum = callerMethodLineNum;
    }
}
