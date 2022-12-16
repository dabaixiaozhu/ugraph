package com.winit.graphgen.entity.graph;

public class MethodAttribute {

    private boolean abstractMethod;

    private boolean publicMethod;

    private boolean protectedMethod;

    public boolean isAbstractMethod() {
        return abstractMethod;
    }

    public void setAbstractMethod(boolean abstractMethod) {
        this.abstractMethod = abstractMethod;
    }

    public boolean isPublicMethod() {
        return publicMethod;
    }

    public void setPublicMethod(boolean publicMethod) {
        this.publicMethod = publicMethod;
    }

    public boolean isProtectedMethod() {
        return protectedMethod;
    }

    public void setProtectedMethod(boolean protectedMethod) {
        this.protectedMethod = protectedMethod;
    }
}
