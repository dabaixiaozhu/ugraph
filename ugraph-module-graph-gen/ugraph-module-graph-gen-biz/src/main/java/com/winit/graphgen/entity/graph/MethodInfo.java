package com.winit.graphgen.entity.graph;

import org.apache.bcel.generic.Type;


public class MethodInfo {

    private String className;

    private String methodName;

    private Type[] methodArgumentTypes;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Type[] getMethodArgumentTypes() {
        return methodArgumentTypes;
    }

    public void setMethodArgumentTypes(Type[] methodArgumentTypes) {
        this.methodArgumentTypes = methodArgumentTypes;
    }
}
