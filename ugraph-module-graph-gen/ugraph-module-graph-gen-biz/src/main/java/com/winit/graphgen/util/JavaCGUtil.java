package com.winit.graphgen.util;

import com.winit.graph.common.entity.JavaCGConstants;
import com.winit.graphgen.entity.graph.MethodInfo;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.Type;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


public class JavaCGUtil {

    private JavaCGUtil() {
        throw new IllegalStateException("illegal");
    }

    public static String getArgListStr(Type[] arguments) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < arguments.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(arguments[i].toString());
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * 获得JavaClass中指定下标的BootstrapMethod
     */
    public static BootstrapMethod getBootstrapMethod(JavaClass javaClass, int index) {
        Attribute[] attributes = javaClass.getAttributes();
        for (Attribute attribute : attributes) {
            if (attribute instanceof BootstrapMethods) {
                BootstrapMethods bootstrapMethods = (BootstrapMethods) attribute;
                BootstrapMethod[] bootstrapMethodArray = bootstrapMethods.getBootstrapMethods();
                if (bootstrapMethodArray != null && bootstrapMethodArray.length > index) {
                    return bootstrapMethodArray[index];
                }
            }
        }

        return null;
    }

    /**
     * 获得BootstrapMethod的方法信息
     */
    public static MethodInfo getBootstrapMethodInfo(BootstrapMethod bootstrapMethod, JavaClass javaClass) {
        for (int argIndex : bootstrapMethod.getBootstrapArguments()) {
            Constant constantArg = javaClass.getConstantPool().getConstant(argIndex);
            if (!(constantArg instanceof ConstantMethodHandle)) {
                continue;
            }

            MethodInfo methodInfo = getMethodFromConstantMethodHandle((ConstantMethodHandle) constantArg, javaClass);
            if (methodInfo != null) {
                return methodInfo;
            }
        }

        return null;
    }

    /**
     * 根据ConstantMethodHandle获得Method对象
     *
     * @param constantMethodHandle
     * @param javaClass
     * @return
     */
    public static MethodInfo getMethodFromConstantMethodHandle(ConstantMethodHandle constantMethodHandle, JavaClass javaClass) {
        ConstantPool constantPool = javaClass.getConstantPool();

        Constant constantCp = constantPool.getConstant(constantMethodHandle.getReferenceIndex());
        if (!(constantCp instanceof ConstantCP)) {
            System.err.println("### 不满足instanceof ConstantCP " + constantCp.getClass().getName());
            return null;
        }

        ConstantCP constantClassAndMethod = (ConstantCP) constantCp;
        String className = constantPool.getConstantString(constantClassAndMethod.getClassIndex(), Const.CONSTANT_Class);
        className = Utility.compactClassName(className, false);

        Constant constantNat = constantPool.getConstant(constantClassAndMethod.getNameAndTypeIndex());
        if (!(constantNat instanceof ConstantNameAndType)) {
            System.err.println("### 不满足instanceof ConstantNameAndType " + constantNat.getClass().getName());
            return null;
        }
        ConstantNameAndType constantNameAndType = (ConstantNameAndType) constantNat;
        String methodName = constantPool.constantToString(constantNameAndType.getNameIndex(), Const.CONSTANT_Utf8);
        String methodArgs = constantPool.constantToString(constantNameAndType.getSignatureIndex(), Const.CONSTANT_Utf8);

        if (methodName != null && methodArgs != null) {
            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setClassName(className);
            methodInfo.setMethodName(methodName);
            Type[] types = Type.getArgumentTypes(methodArgs);
            methodInfo.setMethodArgumentTypes(types);
            return methodInfo;
        }

        System.err.println("### 获取方法信息失败 " + javaClass.getClassName() + " " + className + " " + methodName + " " + methodArgs);
        return null;
    }

    /**
     * 生成格式化后的完整方法名
     *
     * @param fullClassName 完整类名
     * @param methodName    方法名，不包含()
     * @param methodArgs    方法参数，包含起始的()，参数类名之间需要使用半角逗号,分隔，不能包含空格，参数类名也需要为完整类名
     * @return
     */
    public static String formatFullMethod(String fullClassName, String methodName, String methodArgs) {
        return fullClassName + ":" + methodName + methodArgs;
    }

    /**
     * 生成格式化后的方法调用关系
     *
     * @param callId           方法调用序号
     * @param callerFullMethod 调用方法完整方法名
     * @param callType         调用类型
     * @param calleeClassName  被调用方法
     * @param calleeMethodName 被调用方法方法名，说明同上
     * @param calleeMethodArgs 被调用方法方法参数，说明同上
     * @return
     */
    public static String formatMethodCall(int callId, String callerFullMethod, String callType, String calleeClassName, String calleeMethodName, String calleeMethodArgs) {
        return JavaCGConstants.FILE_KEY_METHOD_PREFIX + String.format("%d %s (%s)%s:%s%s", callId, callerFullMethod, callType, calleeClassName, calleeMethodName, calleeMethodArgs);
    }
}