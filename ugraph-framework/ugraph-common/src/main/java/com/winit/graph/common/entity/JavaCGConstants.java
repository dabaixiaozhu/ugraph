package com.winit.graph.common.entity;


import cn.hutool.core.collection.CollUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class JavaCGConstants {

    // —————————————————— trunk表名相关 ——————————————————
    /**
     * jar的相关信息
     */
    public static final String TABLE_JAR_INFO = "graph_app_info";

    /**
     * 类的调用关系，就是这个类，调用了哪些其余的类
     */
    public static final String TABLE_CLASS_CALL = "graph_class_call";

    /**
     * 类的详细信息
     */
    public static final String TABLE_CLASS_DETAIL = "graph_class_detail";

    /**
     * 方法的调用关系，最主要的就是这张表
     */
    public static final String TABLE_METHOD_CALL = "graph_method_call";

    /**
     * 方法详情
     */
    public static final String TABLE_METHOD_DETAIL = "graph_method_detail";


    /**
     * 获取 表名集合
     */
    public static List<String> getTableList() {
        if (CollUtil.isEmpty(TABLE_LIST)) {
            TABLE_LIST.add(TABLE_JAR_INFO);
            TABLE_LIST.add(TABLE_CLASS_CALL);
            TABLE_LIST.add(TABLE_CLASS_DETAIL);
            TABLE_LIST.add(TABLE_METHOD_CALL);
            TABLE_LIST.add(TABLE_METHOD_DETAIL);
        }
        return TABLE_LIST;
    }

    // —————————————————— 分析的前缀 ——————————————————
    /**
     * class
     */
    public static final String FILE_KEY_CLASS_PREFIX = "C:";

    /**
     * class_name
     */
    public static final String FILE_KEY_CLASS_NAME_PREFIX = "CN:";

    /**
     * method
     */
    public static final String FILE_KEY_METHOD_PREFIX = "M:";

    /**
     * method_name
     */
    public static final String FILE_KEY_METHOD_NAME_PREFIX = "MN:";
    /**
     * jar
     */
    public static final String FILE_KEY_JAR_INFO_PREFIX = "J:";
    /**
     * 目录绝对路径
     */
    public static final String FLAG_LAMBDA = "lambda$";
    public static final String FLAG_ARRAY = "[]";
    public static final int DEFAULT_ZERO_NUMBER = 0;
    public static final int NONE_LINE_NUMBER = -1;
    public static final String METHOD_NAME_INIT = "<init>";
    public static final String METHOD_NAME_START = "start";
    public static final String NEW_LINE = "\n";
    /**
     * 合并后的jar的后缀
     */
    public static final String EXT_JAR = ".jar";
    public static final String EXT_WAR = ".war";
    public static final String EXT_CLASS = ".class";
    public static final String EXT_TXT = ".txt";
    public static final String TRUNK_NAME = "TRUNK";
    public static final String ANNOTATION_NULL = "NULL";
    public static final String INIT_METHOD = "<init>";
    public static final String CLINIT_METHOD = "<clinit>";

    /**
     * 自定义文件夹命名
     */
    public static final String TRUNK_PATH = "graphtrunk";
    public static final String INIT_PATH = "graphinit";
    public static final String VERSION_PATH = "graphversion";
    public static final String UGRAPH_ANALYSIS_NAME = "Ugraph-Analysis-Name";

    /**
     * 初始化的标识
     */
    public static final String INIT_TAG = "INIT";

    public static final Integer NUM_FIVE = 5;
    public static final Integer NUM_THREE = 3;
    public static final int INIT_SIZE_1000 = 1000;
    public static final int INIT_SIZE_100 = 100;

    public static final String FLAG_SPACE = " ";
    public static final String FLAG_JAR = "-";
    public static final String FLAG_SEP = "_";
    public static final String FLAG_CAT = ",";
    public static final int FILE_KEY_PREFIX_LENGTH = 2;
    public static final String FLAG_DOT = ".";
    public static final String FLAG_COLON = ":";
    public static final String FLAG_SEMI = ";";
    public static final String FLAG_LEFT_BRACKET = "(";
    public static final String FLAG_RIGHT_BRACKET = ")";
    /**
     * 用于分割系统分隔符
     */
    public static final String COMMON_SEPARATOR = "/|\\\\";

    public static final String FILTER_PACKAGE_NAME = "com.winit";
    public static final String MERGE_FILE_NAME = "MERGE";
    public static final Integer TRUNK_FLAG = 0;
    public static final Integer VERSION_FLAG = 1;

    /**
     * 自定义类注解的名称
     */
    public static final String GRAPH_CLASS_ANNOTATION_NAME = "com/winit/graph/annotation/GraphClassDoc";

    /**
     * 自定义方法注解的名称
     */
    public static final String GRAPH_METHOD_ANNOTATION_NAME = "com/winit/graph/annotation/GraphMethodDoc";

    /**
     * 表名的集合
     */
    private static final CopyOnWriteArrayList<String> TABLE_LIST = new CopyOnWriteArrayList<>();

    private JavaCGConstants() {
        throw new IllegalStateException("illegal");
    }

}
