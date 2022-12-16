package com.winit.graphgen.stat;

import cn.hutool.core.util.StrUtil;
import com.winit.graph.common.entity.CallTypeEnum;
import com.winit.graph.common.entity.JavaCGConstants;
import com.winit.graphgen.entity.graph.CallIdCounter;
import com.winit.graphgen.entity.graph.MethodCallInfoDto;
import com.winit.graphgen.entity.graph.MethodInfo;
import com.winit.graphgen.util.JavaCGUtil;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.winit.graph.common.entity.JavaCGConstants.FILTER_PACKAGE_NAME;

// 处理Method对象
public class MethodVisitor extends EmptyVisitor {

    private final JavaClass javaClass;
    private final MethodGen mg;
    private final ConstantPoolGen cpg;
    private final LineNumberTable lineNumberTable;
    private List<MethodCallInfoDto> methodCallList;
    private InstructionHandle ih;
    private String callerFullMethod;
    private Map<String, Set<String>> calleeMethodMapGlobal;
    private Map<String, Boolean> runnableImplClassMap;
    private Map<String, Boolean> callableImplClassMap;
    private Map<String, Boolean> threadChildClassMap;
    private CallIdCounter callIdCounter;
    private boolean recordAll = false;

    public MethodVisitor(MethodGen mg, JavaClass javaClass) {
        this.javaClass = javaClass;
        this.mg = mg;
        cpg = mg.getConstantPool();

        lineNumberTable = mg.getLineNumberTable(cpg);
    }

    public void beforeStart(Integer maxAppNum) {
        // 生成格式化后的完整方法名
        callerFullMethod = JavaCGUtil.formatFullMethod(javaClass.getClassName(), mg.getName(), JavaCGUtil.getArgListStr(mg.getArgumentTypes()));
    }

    public void start() {
        if (mg.isAbstract() || mg.isNative()) {
            return;
        }

        for (ih = mg.getInstructionList().getStart(); ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();

            if (i instanceof InvokeInstruction) {
                // 在这里进行
                i.accept(this);
            }
        }
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
        addMethodCalls("M", i.getReferenceType(cpg).toString(), i.getMethodName(cpg), i.getArgumentTypes(cpg));
    }

    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
        addMethodCalls("I", i.getReferenceType(cpg).toString(), i.getMethodName(cpg), i.getArgumentTypes(cpg));
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL i) {
        addMethodCalls("O", i.getReferenceType(cpg).toString(), i.getMethodName(cpg), i.getArgumentTypes(cpg));
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC i) {
        addMethodCalls("S", i.getReferenceType(cpg).toString(), i.getMethodName(cpg), i.getArgumentTypes(cpg));
    }

    @Override
    public void visitINVOKEDYNAMIC(INVOKEDYNAMIC i) {
        addMethodCalls("D", i.getType(cpg).toString(), i.getMethodName(cpg), i.getArgumentTypes(cpg));

        Constant constant = cpg.getConstant(i.getIndex());
        if (!(constant instanceof ConstantInvokeDynamic)) {
            return;
        }

        // 处理Lambda表达式
        ConstantInvokeDynamic cid = (ConstantInvokeDynamic) constant;
        // 获得JavaClass中指定下标的BootstrapMethod
        BootstrapMethod bootstrapMethod = JavaCGUtil.getBootstrapMethod(javaClass, cid.getBootstrapMethodAttrIndex());
        if (bootstrapMethod == null) {
            System.err.println("### 无法找到bootstrapMethod " + javaClass.getClassName() + " " + cid.getBootstrapMethodAttrIndex());
            return;
        }

        // 获得BootstrapMethod的方法信息
        MethodInfo bootstrapMethodInfo = JavaCGUtil.getBootstrapMethodInfo(bootstrapMethod, javaClass);
        if (bootstrapMethodInfo == null) {
            System.err.println("### 无法找到bootstrapMethod的方法信息 " + javaClass.getClassName() + " " + bootstrapMethod);
            return;
        }

        String callType = bootstrapMethodInfo.getMethodName().startsWith(JavaCGConstants.FLAG_LAMBDA) ? CallTypeEnum.CTE_LM.getType() : CallTypeEnum.CTE_ST.getType();
        addMethodCalls(callType, bootstrapMethodInfo.getClassName(), bootstrapMethodInfo.getMethodName(), bootstrapMethodInfo.getMethodArgumentTypes());
    }

    // 记录方法调用信息
    private void addMethodCalls(String callType, String calleeClassName, String calleeMethodName, Type[] arguments) {
        if (StrUtil.isEmpty(calleeClassName) || !calleeClassName.startsWith(FILTER_PACKAGE_NAME)) {
            return;
        }

        // 添加被调用方法信息
        String calleeMethodArgs = JavaCGUtil.getArgListStr(arguments);

        if (!recordAll && !calleeClassName.startsWith("java.")) {
            // 记录非java.包中类被调用的方法
            Set<String> calleeMethodWithArgsSet = calleeMethodMapGlobal.computeIfAbsent(calleeClassName, k -> new HashSet<>());
            calleeMethodWithArgsSet.add(calleeMethodName + calleeMethodArgs);
        }

        // 判断是否需要记录线程相关的方法调用
        if (addMethodCall4Thread(calleeClassName, calleeMethodName, calleeMethodArgs)) {
            return;
        }

        String methodCall = JavaCGUtil.formatMethodCall(callIdCounter.addAndGet(), callerFullMethod, callType, calleeClassName, calleeMethodName, calleeMethodArgs);
        MethodCallInfoDto methodCallInfoDto = MethodCallInfoDto.genInstance(methodCall, getSourceLine());
        methodCallList.add(methodCallInfoDto);
    }

    /**
     * 记录线程相关的方法调用
     *
     * @return true: 不记录原始的方法调用类型，false: 记录原始的方法调用类型
     */
    private boolean addMethodCall4Thread(String calleeClassName, String calleeMethodName, String calleeMethodArgs) {
        boolean skipRawMethodCall = false;

        if (JavaCGConstants.METHOD_NAME_INIT.equals(calleeMethodName)) {
            // 处理Runnable实现类
            Boolean recordedRunnable = runnableImplClassMap.get(calleeClassName);
            if (recordedRunnable != null) {
                // 不记录原始调用类型
                skipRawMethodCall = true;
                // 记录其他方法调用Runnable实现类的<init>方法
                String methodCall = JavaCGUtil.formatMethodCall(callIdCounter.addAndGet(), callerFullMethod, CallTypeEnum.CTE_RIR.getType(), calleeClassName, calleeMethodName,
                        calleeMethodArgs);
                MethodCallInfoDto methodCallInfoDto1 = MethodCallInfoDto.genInstance(methodCall, getSourceLine());
                methodCallList.add(methodCallInfoDto1);

                if (Boolean.FALSE.equals(recordedRunnable)) {
                    // Runnable实现类的<init>方法调用Runnable实现类的run方法
                    String tmpCallerFullMethod = JavaCGUtil.formatFullMethod(calleeClassName, calleeMethodName, calleeMethodArgs);
                    String runnableImplClassMethod = JavaCGUtil.formatMethodCall(callIdCounter.addAndGet(), tmpCallerFullMethod, CallTypeEnum.CTE_RIR.getType(), calleeClassName,
                            "run", "()");
                    MethodCallInfoDto methodCallInfoDto2 = MethodCallInfoDto.genInstance(runnableImplClassMethod, JavaCGConstants.DEFAULT_ZERO_NUMBER);
                    methodCallList.add(methodCallInfoDto2);
                    // 避免<init>方法调用run()方法被添加多次
                    runnableImplClassMap.put(calleeClassName, Boolean.TRUE);
                }
            }

            // 处理Callable实现类
            Boolean recordedCallable = callableImplClassMap.get(calleeClassName);
            if (recordedCallable != null) {
                // 不记录原始调用类型
                skipRawMethodCall = true;
                // 记录其他方法调用Callable实现类的<init>方法
                String methodCall = JavaCGUtil.formatMethodCall(callIdCounter.addAndGet(), callerFullMethod, CallTypeEnum.CTE_CIC.getType(), calleeClassName, calleeMethodName,
                        calleeMethodArgs);
                MethodCallInfoDto methodCallInfoDto1 = MethodCallInfoDto.genInstance(methodCall, getSourceLine());
                methodCallList.add(methodCallInfoDto1);

                if (Boolean.FALSE.equals(recordedCallable)) {
                    // Callable实现类的<init>方法调用Callable实现类的call方法
                    String tmpCallerFullMethod = JavaCGUtil.formatFullMethod(calleeClassName, calleeMethodName, calleeMethodArgs);
                    String callableImplClassMethod = JavaCGUtil.formatMethodCall(callIdCounter.addAndGet(), tmpCallerFullMethod, CallTypeEnum.CTE_CIC.getType(), calleeClassName,
                            "call", "()");
                    MethodCallInfoDto methodCallInfoDto2 = MethodCallInfoDto.genInstance(callableImplClassMethod, JavaCGConstants.DEFAULT_ZERO_NUMBER);
                    methodCallList.add(methodCallInfoDto2);
                    // 避免<init>方法调用call()方法被添加多次
                    callableImplClassMap.put(calleeClassName, Boolean.TRUE);
                }
            }
        } else if (JavaCGConstants.METHOD_NAME_START.equals(calleeMethodName) && "()".equals(calleeMethodArgs)) {
            // 处理Thread子类
            if (Boolean.FALSE.equals(threadChildClassMap.get(calleeClassName))) {
                // 记录Thread子类的start方法调用run方法（以上Map的value等于FALSE时，代表当前类为Thread的子类，且start()方法调用run()方法未添加过）
                String tmpCallerFullMethod = JavaCGUtil.formatFullMethod(calleeClassName, calleeMethodName, calleeMethodArgs);
                String threadChildClassMethod = JavaCGUtil.formatMethodCall(callIdCounter.addAndGet(), tmpCallerFullMethod, CallTypeEnum.CTE_TSR.getType(), calleeClassName, "run"
                        , "()");
                MethodCallInfoDto methodCallInfoDto2 = MethodCallInfoDto.genInstance(threadChildClassMethod, JavaCGConstants.DEFAULT_ZERO_NUMBER);
                methodCallList.add(methodCallInfoDto2);
                // 避免start()方法调用run()方法被添加多次
                threadChildClassMap.put(calleeClassName, Boolean.TRUE);
            }
        }

        return skipRawMethodCall;
    }

    // 获取源代码行号
    private int getSourceLine() {
        if (lineNumberTable == null) {
            return JavaCGConstants.DEFAULT_ZERO_NUMBER;
        }
        int sourceLine = lineNumberTable.getSourceLine(ih.getPosition());
        return Math.max(sourceLine, 0);
    }

    public void setMethodCallList(List<MethodCallInfoDto> methodCallList) {
        this.methodCallList = methodCallList;
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
