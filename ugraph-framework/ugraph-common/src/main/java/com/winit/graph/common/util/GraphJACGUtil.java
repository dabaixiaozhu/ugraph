package com.winit.graph.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import static com.winit.graph.common.entity.JavaCGConstants.*;


public class GraphJACGUtil {

    private GraphJACGUtil() {
        throw new IllegalStateException("illegal");
    }

    /**
     * 从完整类名中获取简单类名（去掉包名）
     *
     * @param fullClassName 完整类名
     * @return
     */
    public static String getSimpleClassNameFromFull(String fullClassName) {
        int indexLastDot = fullClassName.lastIndexOf(FLAG_DOT);
        if (indexLastDot == -1) {
            return fullClassName;
        }
        return fullClassName.substring(indexLastDot + 1);
    }

    /**
     * 从完整方法信息中获取方法名
     *
     * @param method 完整方法信息
     * @return
     */
    public static String getOnlyMethodName(String method) {
        int indexColon = method.indexOf(FLAG_COLON);
        int indexLeftBrackets = method.indexOf(FLAG_LEFT_BRACKET);
        return method.substring(indexColon + 1, indexLeftBrackets);
    }

    /**
     * 从完整方法信息中获取完整类名
     *
     * @param method 完整方法信息
     * @return
     */
    public static String getFullClassNameFromMethod(String method) {
        int indexLastColon = method.lastIndexOf(FLAG_COLON);
        return method.substring(0, indexLastColon);
    }

    public static boolean isNumStr(String str) {
        char[] charArray = str.toCharArray();
        for (char ch : charArray) {
            if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }

    public static String genHashWithLen(String data) {
        byte[] md5 = DigestUtils.md5(data);
        // 以下使用的BASE64方法输出结果范围为字母+“+”+“/”，不是原始的字母+“-”+“_”
        return String.format("%s#%03x", Base64.encodeBase64URLSafeString(md5), data.length());
    }
}
