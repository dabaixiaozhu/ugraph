package com.winit.graphgen.stat;

import com.winit.graphgen.entity.graph.CallIdCounter;
import com.winit.graphgen.entity.graph.MethodCallInfoDto;
import com.winit.graphgen.entity.graph.MethodLineNumberInfo;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.winit.graph.common.entity.JavaCGConstants.CLINIT_METHOD;
import static com.winit.graph.common.entity.JavaCGConstants.INIT_METHOD;

// 处理Class对象
public class ClassVisitor {

    private static final Logger log = LoggerFactory.getLogger(ClassVisitor.class);
    private final JavaClass javaClass;
    private final ConstantPoolGen cpg;
    private final List<MethodCallInfoDto> methodCallList = new ArrayList<>(200);
    private final List<MethodLineNumberInfo> methodLineNumberList = new ArrayList<>(100);

    private Map<String, Set<String>> calleeMethodMapGlobal;
    private Map<String, Boolean> runnableImplClassMap;
    private Map<String, Boolean> callableImplClassMap;
    private Map<String, Boolean> threadChildClassMap;
    private CallIdCounter callIdCounter;
    private boolean recordAll = false;

    public ClassVisitor(JavaClass javaClass) {
        this.javaClass = javaClass;
        cpg = new ConstantPoolGen(javaClass.getConstantPool());
    }

    public void visitMethod(Method method, Integer maxAppNum) {
        try {
            MethodGen mg = new MethodGen(method, javaClass.getClassName(), cpg);
            MethodVisitor visitor = new MethodVisitor(mg, javaClass);
            visitor.setMethodCallList(methodCallList);
            visitor.setCalleeMethodMapGlobal(calleeMethodMapGlobal);
            visitor.setRunnableImplClassMap(runnableImplClassMap);
            visitor.setCallableImplClassMap(callableImplClassMap);
            visitor.setThreadChildClassMap(threadChildClassMap);
            visitor.setCallIdCounter(callIdCounter);
            visitor.setRecordAll(recordAll);
            visitor.beforeStart(maxAppNum);
            visitor.start();
        } catch (Exception e) {
            log.error("处理方法出现异常 " + javaClass.getClassName() + " " + method.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 处理方法的调用关系
     */
    public void start(Integer maxAppNum) {
        for (Method method : javaClass.getMethods()) {
            if (method.getName().contains(INIT_METHOD) || method.getName().contains(CLINIT_METHOD)) {
                continue;
            }
            visitMethod(method, maxAppNum);
        }
    }

    public List<MethodCallInfoDto> getMethodCallList() {
        return methodCallList;
    }

    public List<MethodLineNumberInfo> getMethodLineNumberList() {
        return methodLineNumberList;
    }

    public void setCalleeMethodMapGlobal(Map<String, Set<String>> calleeMethodMapGlobal) {
        this.calleeMethodMapGlobal = calleeMethodMapGlobal;
    }

    public void setRunnableImplClassMap(Map<String, Boolean> runnableImplClassMap) {
        this.runnableImplClassMap = runnableImplClassMap;
    }

    public void setCallableImplClassMap(Map<String, Boolean> callableImplClassMap) {
        this.callableImplClassMap = callableImplClassMap;
    }

    public void setThreadChildClassMap(Map<String, Boolean> threadChildClassMap) {
        this.threadChildClassMap = threadChildClassMap;
    }

    public void setCallIdCounter(CallIdCounter callIdCounter) {
        this.callIdCounter = callIdCounter;
    }

    public void setRecordAll(boolean recordAll) {
        this.recordAll = recordAll;
    }

}
