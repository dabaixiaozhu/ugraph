package com.winit.graphgen.entity.graph;


public class MethodLineNumberInfo {
    private String fullMethod;

    private int minLineNumber;

    private int maxLineNumber;

    public String getFullMethod() {
        return fullMethod;
    }

    public void setFullMethod(String fullMethod) {
        this.fullMethod = fullMethod;
    }

    public int getMinLineNumber() {
        return minLineNumber;
    }

    public void setMinLineNumber(int minLineNumber) {
        this.minLineNumber = minLineNumber;
    }

    public int getMaxLineNumber() {
        return maxLineNumber;
    }

    public void setMaxLineNumber(int maxLineNumber) {
        this.maxLineNumber = maxLineNumber;
    }
}
