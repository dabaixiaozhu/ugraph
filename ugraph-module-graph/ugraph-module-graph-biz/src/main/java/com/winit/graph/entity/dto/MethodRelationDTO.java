package com.winit.graph.entity.dto;

/**
 * 返回调用路径 DTO
 */
public class MethodRelationDTO {

    /**
     * 序号
     * id
     */
    private Long id;

    /**
     * 调用类型
     * call_type
     */
    private String callType;

    /**
     * 调用方，方法hash+字节数
     * caller_method_hash
     */
    private String callerMethodHash;

    /**
     * 调用方，源代码行号
     * method_line_num
     */
    private Integer callerMethodLineNum;

    /**
     * 被调用方，方法hash+字节数
     * callee_method_hash
     */
    private String calleeMethodHash;

    /**
     * app_num
     */
    private Integer appNum;

    /**
     * 简单方法名
     * method_name
     */
    private String methodName;

    /**
     * 注解属性，JSON字符串格式
     * annotation_text
     */
    private String annotationText;

    /**
     * 类hash
     * class_hash
     */
    private String classHash;

    /**
     * 全类名
     * class_full_name
     */
    private String classFullName;

    /**
     * 完整的方法名
     * method_full_name
     */
    private String methodFullName;

    /**
     * 0表示未修改，1表示新增，2表示修改
     */
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMethodFullName() {
        return methodFullName;
    }

    public void setMethodFullName(String methodFullName) {
        this.methodFullName = methodFullName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCalleeMethodHash() {
        return calleeMethodHash;
    }

    public void setCalleeMethodHash(String calleeMethodHash) {
        this.calleeMethodHash = calleeMethodHash;
    }

    public Integer getAppNum() {
        return appNum;
    }

    public void setAppNum(Integer appNum) {
        this.appNum = appNum;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getAnnotationText() {
        return annotationText;
    }

    public void setAnnotationText(String annotationText) {
        this.annotationText = annotationText;
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
