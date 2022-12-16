package com.winit.graph.entity.vo;

import java.util.List;

/**
 * 返回调用路径VO
 */
public class MethodRelationVO {

    /**
     * 就是 methodHash，全局唯一，方便前端的展示
     */
    private String id;

    /**
     * 就是 methodName，方便前端的展示
     */
    private String label;

    /**
     * 被调用的子节点
     */
    private List<MethodRelationVO> children;

    /**
     * 当前调用分所属的项目
     */
    private Integer appNum;

    /**
     * 调用节点，调用类型
     */
    private String callType;

    /**
     * 调用方，源代码行号
     * caller_method_line_num
     */
    private Integer methodLineNum;

    /**
     * 调用节点，方法hash+字节数
     */
    private String methodHash;

    /**
     * 调用节点，方法名（简易）
     */
    private String methodName;

    /**
     * 调用节点，方法名（完整）
     */
    private String methodFullName;

    /**
     * 注解内容
     */
    private String annotation;

    /**
     * 全类名
     */
    private String classFullName;

    /**
     * 是否还有往下的递归
     * true标识有，false表示没有
     */
    private Boolean end;

    /**
     * 0表示未修改，1表示新增，2表示修改
     */
    private Integer status = 0;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getEnd() {
        return end;
    }

    public void setEnd(Boolean end) {
        this.end = end;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<MethodRelationVO> getChildren() {
        return children;
    }

    public void setChildren(List<MethodRelationVO> children) {
        this.children = children;
    }

    public Integer getMethodLineNum() {
        return methodLineNum;
    }

    public void setMethodLineNum(Integer methodLineNum) {
        this.methodLineNum = methodLineNum;
    }

    public Integer getAppNum() {
        return appNum;
    }

    public void setAppNum(Integer appNum) {
        this.appNum = appNum;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
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

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
}
