package com.winit.graphgen.entity.graph;

import java.util.List;

public class ClassInterfaceMethodInfo {

    private List<String> interfaceNameList;

    private List<String> methodWithArgsList;

    public List<String> getInterfaceNameList() {
        return interfaceNameList;
    }

    public void setInterfaceNameList(List<String> interfaceNameList) {
        this.interfaceNameList = interfaceNameList;
    }

    public List<String> getMethodWithArgsList() {
        return methodWithArgsList;
    }

    public void setMethodWithArgsList(List<String> methodWithArgsList) {
        this.methodWithArgsList = methodWithArgsList;
    }
}
