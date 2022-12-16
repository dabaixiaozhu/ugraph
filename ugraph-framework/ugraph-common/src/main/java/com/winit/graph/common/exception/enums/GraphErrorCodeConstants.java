package com.winit.graph.common.exception.enums;


import com.winit.graph.common.exception.ErrorCode;

/**
 * demo 错误码枚举类
 * 使用 1-0001-000-000 段
 */
public interface GraphErrorCodeConstants {

    // —————————— 测试模块 ——————————
    ErrorCode TEST_NUM_NOT_EXISTS = new ErrorCode(1001000001, "测试的数字不存在");

    // —————————— graph模块 ——————————
    ErrorCode GEN_JAR_RELATION_FAIL = new ErrorCode(1001001001, "生成调用关系失败");
    ErrorCode JAR_PATH_NOT_EXIST = new ErrorCode(1001001002, "jar包路径不能为空");
    ErrorCode INIT_JAR_RELATION_ING = new ErrorCode(1001001004, "正在初始化调用关系，请稍后尝试");
    ErrorCode DUPLICATE_ADD_FAIL = new ErrorCode(1001001005, "请勿重复插入数据");
    ErrorCode JAR_PATH_INVALID = new ErrorCode(1001001006, "输入路径无效");
    ErrorCode CLASS_PATH_INVALID = new ErrorCode(1001001007, "输入的类路径无效，当前项目中无此类");
    ErrorCode GEN_TRUNK_RELATION_ING = new ErrorCode(1001001008, "正在生成应用的trunk调用关系，请稍后尝试");
    ErrorCode GEN_VERSION_RELATION_ING = new ErrorCode(1001001008, "正在生成应用的version调用关系，请稍后尝试");
    ErrorCode APP_NOT_EXIT = new ErrorCode(1001001009, "当前工程不存在");

    // —————————— 远程调用beetle ——————————
    ErrorCode GET_REMOTE_REQUEST_FAIL = new ErrorCode(1002001001, "远程获取信息失败");
}
