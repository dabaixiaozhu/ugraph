package com.winit.graphgen.entity.graph;


public class MethodCallInfoDto {

    private String methodCall;

    private int sourceLine;

    public static MethodCallInfoDto genInstance(String methodCall, int sourceLine) {
        MethodCallInfoDto methodCallInfoDto = new MethodCallInfoDto();
        methodCallInfoDto.setMethodCall(methodCall);
        methodCallInfoDto.setSourceLine(sourceLine);
        return methodCallInfoDto;
    }

    public String getMethodCall() {
        return methodCall;
    }

    public void setMethodCall(String methodCall) {
        this.methodCall = methodCall;
    }

    public int getSourceLine() {
        return sourceLine;
    }

    public void setSourceLine(int sourceLine) {
        this.sourceLine = sourceLine;
    }
}
