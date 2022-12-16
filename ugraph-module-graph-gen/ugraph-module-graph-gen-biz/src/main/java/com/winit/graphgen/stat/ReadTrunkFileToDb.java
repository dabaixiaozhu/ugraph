package com.winit.graphgen.stat;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.winit.graph.common.entity.JavaCGConstants;
import com.winit.graph.common.util.GraphJACGUtil;
import com.winit.graphgen.entity.dto.ClassCallDto;
import com.winit.graphgen.entity.dto.ClassDetailDto;
import com.winit.graphgen.entity.dto.MethodCallDto;
import com.winit.graphgen.entity.dto.MethodDetailDto;
import com.winit.graphgen.mapper.GraphGenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.winit.graph.common.entity.JavaCGConstants.*;

/**
 * 读取文件到db的类
 *
 * @Author zeyu.lin  00018326
 * @Date 16:26 2022/6/14
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ReadTrunkFileToDb {

    private static final Logger log = LoggerFactory.getLogger(ReadTrunkFileToDb.class);

    @Autowired
    private GraphGenMapper graphGenMapper;

    /**
     * 插入 graph_class_call 的内容
     */
    private final List<ClassCallDto> classCallList = new ArrayList<>(INIT_SIZE_1000);

    /**
     * 插入 graph_class_detail 的内容
     */
    private final HashMap<String, ClassDetailDto> classDetailMap = new HashMap<>(INIT_SIZE_1000);

    /**
     * 插入 graph_method_call 的内容
     */
    private final List<MethodCallDto> methodCallList = new ArrayList<>(INIT_SIZE_1000);

    /**
     * 插入 graph_method_detail 的内容
     */
    private final HashMap<String, MethodDetailDto> methodDetailMap = new HashMap<>(INIT_SIZE_1000);

    /**
     * 插入 graph_class_call_version 的内容
     */
    private final List<ClassCallDto> versionClassCallList = new ArrayList<>(INIT_SIZE_1000);

    /**
     * 插入 graph_class_detail_version 的内容
     */
    private final HashMap<String, ClassDetailDto> versionClassDetailMap = new HashMap<>(INIT_SIZE_1000);

    /**
     * 插入 graph_method_call_version 的内容
     */
    private final List<MethodCallDto> versionMethodCallList = new ArrayList<>(INIT_SIZE_1000);

    /**
     * 插入 graph_method_detail_version 的内容
     */
    private final HashMap<String, MethodDetailDto> versionMethodDetailMap = new HashMap<>(INIT_SIZE_1000);

    /**
     * 拼接MethodDetail的入参
     */
    private MethodDetailDto getMethodDetailParam(String[] methodInfoArray) {
        String methodFullName = methodInfoArray[0].split(FILE_KEY_METHOD_NAME_PREFIX)[1];
        String methodName = GraphJACGUtil.getOnlyMethodName(methodFullName);
        Integer appNum = Integer.valueOf(methodInfoArray[1]);
        String methodHash = GraphJACGUtil.genHashWithLen(methodFullName);
        String classFullName = GraphJACGUtil.getFullClassNameFromMethod(methodFullName);
        String annotationText = ANNOTATION_NULL.equals(methodInfoArray[2]) ? null : methodInfoArray[2];

        // 插入 graph_method_detail
        MethodDetailDto methodDetailDto = new MethodDetailDto();
        methodDetailDto.setMethodHash(methodHash);
        methodDetailDto.setMethodName(methodName);
        methodDetailDto.setMethodFullName(methodFullName.split(FLAG_COLON)[1]);
        methodDetailDto.setClassFullName(classFullName);
        methodDetailDto.setClassHash(GraphJACGUtil.genHashWithLen(classFullName));
        methodDetailDto.setAppNum(appNum);
        methodDetailDto.setAnnotationText(annotationText);
        return methodDetailDto;
    }

    /**
     * 处理trunk的 merged文件
     */
    public boolean handleTrunkMergedTxt(String mergePath, Integer flag) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(mergePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                // 处理类的调用（C:开头），存入 graph_class_call
                if (line.startsWith(FILE_KEY_CLASS_PREFIX)) {
                    handleOneClassInfo(line, flag);
                }
                // 处理类的调用（CN:开头），存入graph_class_detail
                else if (line.startsWith(FILE_KEY_CLASS_NAME_PREFIX)) {
                    handleOneClassNameInfo(line, flag);
                }
                //  处理类的调用（MN:开头），存入graph_method_detail
                else if (line.startsWith(FILE_KEY_METHOD_NAME_PREFIX)) {
                    handleOneMethodNameInfo(line, flag);
                }
                // 处理一条方法调用(M:开头)，存入 graph_method_call
                else if (line.startsWith(JavaCGConstants.FILE_KEY_METHOD_PREFIX)) {
                    handleOneCallerMethodInfo(line, flag);
                }
            }

            if (flag == 0) {
                // 将剩余的数据存入 graph_class_call
                if (CollUtil.isNotEmpty(classCallList)) {
                    graphGenMapper.patchInsertClassCall(classCallList);
                    classCallList.clear();
                }

                // 将剩余的数据存入 graph_class_detail
                if (MapUtil.isNotEmpty(classDetailMap)) {
                    List<ClassDetailDto> insertClassDetailDtoList = new ArrayList<>(classDetailMap.values());
                    graphGenMapper.patchInsertClassDetail(insertClassDetailDtoList);
                    classDetailMap.clear();
                }

                // 将剩余的数据存入 graph_method_detail
                if (MapUtil.isNotEmpty(methodDetailMap)) {
                    List<MethodDetailDto> insertMethodDetailList = new ArrayList<>(methodDetailMap.values());
                    methodDetailMap.clear();
                    graphGenMapper.patchInsertMethodDetail(insertMethodDetailList);
                }

                // 将剩余的数据存入 graph_method_call
                if (CollUtil.isNotEmpty(methodCallList)) {
                    graphGenMapper.patchInsertMethodCall(methodCallList);
                    methodCallList.clear();
                }
            } else {
                // 将剩余的数据存入 graph_class_call_version
                if (CollUtil.isNotEmpty(versionClassCallList)) {
                    graphGenMapper.patchInsertClassCallVersion(versionClassCallList);
                    versionClassCallList.clear();
                }

                // 将剩余的数据存入 graph_class_detail_version
                if (MapUtil.isNotEmpty(versionClassDetailMap)) {
                    List<ClassDetailDto> insertClassDetailDtoList = new ArrayList<>(versionClassDetailMap.values());
                    graphGenMapper.patchInsertClassDetailVersion(insertClassDetailDtoList);
                    versionClassDetailMap.clear();
                }

                // 将剩余的数据存入 graph_method_detail_version
                if (MapUtil.isNotEmpty(versionMethodDetailMap)) {
                    List<MethodDetailDto> insertMethodDetailList = new ArrayList<>(versionMethodDetailMap.values());
                    versionMethodDetailMap.clear();
                    graphGenMapper.patchInsertMethodDetailVersion(insertMethodDetailList);
                }

                // 将剩余的数据存入 graph_method_call_version
                if (CollUtil.isNotEmpty(versionMethodCallList)) {
                    graphGenMapper.patchInsertMethodCallVersion(versionMethodCallList);
                    versionMethodCallList.clear();
                }
            }

            return true;
        } catch (Exception e) {
            log.error("处理merged文件，出现异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 处理类的调用（MN:开头），存入graph_method_detail
     */
    private void handleOneMethodNameInfo(String line, Integer flag) {
        String[] methodInfoArray = line.split(FLAG_SPACE);
        // 只记录 处理下一行
        if (methodInfoArray.length != NUM_THREE) {
            log.error("方法名信息非法 [{}] [{}]", line, methodInfoArray.length);
            return;
        }

        // 拼接参数
        MethodDetailDto methodDetailDto = getMethodDetailParam(methodInfoArray);
        String methodFullName = methodInfoArray[0].split(FILE_KEY_METHOD_NAME_PREFIX)[1];
        String methodHash = GraphJACGUtil.genHashWithLen(methodFullName);

        if (flag == 0) {
            methodDetailMap.putIfAbsent(methodHash, methodDetailDto);
            // 每1000条插入一次数据库
            if (methodDetailMap.size() >= INIT_SIZE_1000) {
                List<MethodDetailDto> insertMethodDetailList = new ArrayList<>(methodDetailMap.values());
                methodDetailMap.clear();
                graphGenMapper.patchInsertMethodDetail(insertMethodDetailList);
            }
        } else {
            versionMethodDetailMap.putIfAbsent(methodHash, methodDetailDto);
            // 每1000条插入一次数据库
            if (versionMethodDetailMap.size() >= INIT_SIZE_1000) {
                List<MethodDetailDto> insertMethodDetailList = new ArrayList<>(versionMethodDetailMap.values());
                versionMethodDetailMap.clear();
                graphGenMapper.patchInsertMethodDetailVersion(insertMethodDetailList);
            }
        }
    }

    /**
     * 处理类的调用（CN:开头），存入graph_class_detail
     */
    private void handleOneClassNameInfo(String line, Integer flag) {
        String[] classInfoArray = line.split(FLAG_SPACE);
        // 只记录 处理下一行
        if (classInfoArray.length != NUM_THREE) {
            log.error("类名信息非法 [{}] [{}]", line, classInfoArray.length);
            return;
        }

        // 获取参数
        String callerClassFullName = classInfoArray[0].split(FILE_KEY_CLASS_NAME_PREFIX)[1];
        String annotationText = ANNOTATION_NULL.equals(classInfoArray[1]) ? null : classInfoArray[1];
        Integer appNum = Integer.valueOf(classInfoArray[2]);
        String callerClassHash = GraphJACGUtil.genHashWithLen(callerClassFullName);

        // 拼接入表内容 graph_class_detail
        ClassDetailDto classDetailDto = new ClassDetailDto();
        classDetailDto.setClassHash(callerClassHash);
        classDetailDto.setClassFullName(callerClassFullName);
        classDetailDto.setClassName(GraphJACGUtil.getSimpleClassNameFromFull(callerClassFullName));
        classDetailDto.setAppNum(appNum);
        classDetailDto.setAnnotationText(annotationText);

        if (flag == 0) {
            classDetailMap.putIfAbsent(callerClassHash, classDetailDto);
            if (classDetailMap.size() >= INIT_SIZE_1000) {
                ArrayList<ClassDetailDto> insertClassDetailDtoList = new ArrayList<>(classDetailMap.values());
                classDetailMap.clear();
                graphGenMapper.patchInsertClassDetail(insertClassDetailDtoList);
            }
        } else {
            versionClassDetailMap.putIfAbsent(callerClassHash, classDetailDto);
            if (versionClassDetailMap.size() >= INIT_SIZE_1000) {
                ArrayList<ClassDetailDto> insertClassDetailDtoList = new ArrayList<>(versionClassDetailMap.values());
                versionClassDetailMap.clear();
                graphGenMapper.patchInsertClassDetailVersion(insertClassDetailDtoList);
            }
        }
    }

    /**
     * 处理类的调用（C:开头），存入 graph_class_call
     */
    private void handleOneClassInfo(String line, Integer flag) {
        String[] classInfoArray = line.split(FLAG_SPACE);
        // 只记录 处理下一行
        if (classInfoArray.length != NUM_THREE) {
            log.error("类调用信息非法 [{}] [{}]", line, classInfoArray.length);
            return;
        }

        // 获取参数
        String callerClassFullName = classInfoArray[0].split(FILE_KEY_CLASS_PREFIX)[1];
        String calleeClassFullName = classInfoArray[1].substring(classInfoArray[1].indexOf(FLAG_RIGHT_BRACKET) + 1);
        Integer appNum = Integer.valueOf(classInfoArray[2]);

        // 拼接入表内容 graph_class_call
        ClassCallDto classCallDto = new ClassCallDto();
        String callerClassHash = GraphJACGUtil.genHashWithLen(callerClassFullName);
        classCallDto.setCallerClassHash(callerClassHash);
        classCallDto.setCalleeClassFullName(calleeClassFullName);
        classCallDto.setCalleeClassHash(GraphJACGUtil.genHashWithLen(calleeClassFullName));
        classCallDto.setCallerClassFullName(callerClassFullName);
        classCallDto.setAppNum(appNum);
        classCallDto.setCallType(classInfoArray[1].substring(1, classInfoArray[1].indexOf(FLAG_RIGHT_BRACKET)));

        // 表示trunk
        if (flag == 0) {
            classCallList.add(classCallDto);
            if (classCallList.size() >= INIT_SIZE_1000) {
                ArrayList<ClassCallDto> insertClassCallDtoList = new ArrayList<>(classCallList);
                classCallList.clear();
                graphGenMapper.patchInsertClassCall(insertClassCallDtoList);
            }
        } else {
            versionClassCallList.add(classCallDto);
            if (versionClassCallList.size() >= INIT_SIZE_1000) {
                ArrayList<ClassCallDto> insertClassCallDtoList = new ArrayList<>(versionClassCallList);
                versionClassCallList.clear();
                graphGenMapper.patchInsertClassCallVersion(insertClassCallDtoList);
            }
        }
    }

    /**
     * 处理一条方法调用(M:开头)，存入 graph_method_call
     */
    private void handleOneCallerMethodInfo(String line, Integer flag) {
        // 校验不通过，只记录，继续分析下一行
        if (!checkMethodLine(line)) {
            log.error("方法信息非法 [{}]", line);
            return;
        }

        MethodCallDto methodCallDto = getMethodCallParam(line);
        if (methodCallDto == null) {
            return;
        }

        // 插入数据
        if (flag == 0) {
            methodCallList.add(methodCallDto);
            // 每1000条插入一次数据库
            if (methodCallList.size() >= INIT_SIZE_1000) {
                List<MethodCallDto> insertMethodCallList = new ArrayList<>(methodCallList);
                methodCallList.clear();
                graphGenMapper.patchInsertMethodCall(insertMethodCallList);
            }
        } else {
            versionMethodCallList.add(methodCallDto);
            // 每1000条插入一次数据库
            if (versionMethodCallList.size() >= INIT_SIZE_1000) {
                List<MethodCallDto> insertMethodCallList = new ArrayList<>(versionMethodCallList);
                versionMethodCallList.clear();
                graphGenMapper.patchInsertMethodCallVersion(insertMethodCallList);
            }
        }
    }

    /**
     * 拼接MethodCall的入参
     */
    private MethodCallDto getMethodCallParam(String line) {
        // 拼接参数
        String[] methodInfoArray = line.split(FLAG_SPACE);
        String callerFullMethod = methodInfoArray[1];
        String calleeFullMethod = methodInfoArray[2];
        int indexCalleeLeftBracket = calleeFullMethod.indexOf(FLAG_LEFT_BRACKET);
        int indexCalleeRightBracket = calleeFullMethod.indexOf(FLAG_RIGHT_BRACKET);
        String finalCalleeFullMethod = calleeFullMethod.substring(indexCalleeRightBracket + 1).trim();
        String callerMethodHash = GraphJACGUtil.genHashWithLen(callerFullMethod);
        String calleeMethodHash = GraphJACGUtil.genHashWithLen(finalCalleeFullMethod);
        String calleeFullClassName = GraphJACGUtil.getFullClassNameFromMethod(finalCalleeFullMethod);
        if (callerMethodHash.equals(calleeMethodHash)) {
            // 对于递归调用，不写入数据库，防止查询时出现死循环
            return null;
        }

        // 插入 graph_method_call 的参数内容
        MethodCallDto methodCallDto = new MethodCallDto();
        methodCallDto.setCallType(calleeFullMethod.substring(indexCalleeLeftBracket + 1, indexCalleeRightBracket));
        methodCallDto.setCallerMethodHash(callerMethodHash);
        methodCallDto.setCalleeMethodHash(calleeMethodHash);
        methodCallDto.setAppNum(Integer.valueOf(methodInfoArray[4]));
        methodCallDto.setCallerMethodLineNum(Integer.parseInt(methodInfoArray[3]));
        methodCallDto.setMethodName(GraphJACGUtil.getOnlyMethodName(finalCalleeFullMethod));
        methodCallDto.setMethodFullName(finalCalleeFullMethod.split(FLAG_COLON)[1]);
        methodCallDto.setClassHash(GraphJACGUtil.genHashWithLen(GraphJACGUtil.getFullClassNameFromMethod(finalCalleeFullMethod)));
        methodCallDto.setClassFullName(calleeFullClassName);
        return methodCallDto;
    }

    /**
     * 检查方法调用行记录的合法性
     */
    private boolean checkMethodLine(String line) {
        String[] methodInfoArray = line.split(FLAG_SPACE);
        // 只记录 处理下一行
        if (methodInfoArray.length != NUM_FIVE) {
            log.error("方法调用信息非法 [{}] [{}]", line, methodInfoArray.length);
            return false;
        }

        String callIdStr = methodInfoArray[0].substring(FILE_KEY_PREFIX_LENGTH);
        String strCallerLineNum = methodInfoArray[3];
        String callerAppNum = methodInfoArray[4];

        if (!GraphJACGUtil.isNumStr(callIdStr)) {
            log.error("方法调用ID非法 [{}] [{}]", line, callIdStr);
            return false;
        }

        if (!GraphJACGUtil.isNumStr(strCallerLineNum)) {
            log.error("方法调用信息行号非法 [{}] [{}]", line, strCallerLineNum);
            return false;
        }

        if (!GraphJACGUtil.isNumStr(callerAppNum)) {
            log.error("Jar包序号非法 [{}] [{}]", line, callerAppNum);
            return false;
        }
        return true;
    }
}
