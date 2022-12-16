package com.winit.graphgen.entity.graph;

import java.util.Map;


public class ExtendsClassMethodInfo {

    private boolean abstractClass;

    private String superClassName;

    private Map<String, MethodAttribute> methodAttributeMap;

    public boolean isAbstractClass() {
        return abstractClass;
    }

    public void setAbstractClass(boolean abstractClass) {
        this.abstractClass = abstractClass;
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    public Map<String, MethodAttribute> getMethodAttributeMap() {
        return methodAttributeMap;
    }

    public void setMethodAttributeMap(Map<String, MethodAttribute> methodAttributeMap) {
        this.methodAttributeMap = methodAttributeMap;
    }
}
