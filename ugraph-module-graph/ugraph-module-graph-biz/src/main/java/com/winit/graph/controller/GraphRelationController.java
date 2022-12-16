package com.winit.graph.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.winit.graph.annotation.GraphClassDoc;
import com.winit.graph.annotation.GraphMethodDoc;
import com.winit.graph.common.entity.CommonResult;
import com.winit.graph.common.exception.util.ServiceExceptionUtil;
import com.winit.graph.common.util.GraphFileUtil;
import com.winit.graph.entity.inputvo.MethodDownRelationInputVO;
import com.winit.graph.entity.inputvo.MethodUpRelationInputVO;
import com.winit.graph.entity.vo.CommonResultVO;
import com.winit.graph.entity.vo.MethodRelationVO;
import com.winit.graph.entity.vo.MethodUpRelationVO;
import com.winit.graph.service.GraphLogService;
import com.winit.graph.service.GraphRelationService;
import com.winit.graph.service.SingletonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.winit.graph.common.entity.JavaCGConstants.*;
import static com.winit.graph.common.exception.enums.GraphErrorCodeConstants.*;

@RestController
@Validated
@Api(tags = "Graph Controller类，用于获取调用关系")
@GraphClassDoc("Graph Controller类，用于获取调用关系")
@RequestMapping("/graph")
public class GraphRelationController {

    private static final Logger log = LoggerFactory.getLogger(GraphRelationController.class);

    @Autowired
    private GraphRelationService graphRelationService;

    @Autowired
    private GraphLogService graphLogService;

    @Autowired
    private SingletonService singletonService;


    @PostMapping("/initGenerateAppRelations")
    @ApiOperation(value = "initGenerateAppRelations", notes = "初始化生成系统间的调用关系")
    public CommonResult<String> initGenerateAppRelations(@RequestParam("jar_path") String jarPath) {
        // jarPath 不能为空
        if (StrUtil.hasEmpty(jarPath)) {
            throw ServiceExceptionUtil.exception(JAR_PATH_NOT_EXIST);
        }

        // jarPath这个路径集合，必须都是有效的路径
        String[] array = GraphFileUtil.getJarArray(jarPath, FLAG_SPACE);
        if (!GraphFileUtil.isValidPath(array)) {
            throw ServiceExceptionUtil.exception(JAR_PATH_INVALID);
        }

        // 正在生成关系的话，既不能查询也不能重新生成关系
        synchronized (this) {
            if (singletonService.getIsInitGeneratingTrunkGraphRelation()) {
                throw ServiceExceptionUtil.exception(INIT_JAR_RELATION_ING);
            }
            singletonService.setIsInitGeneratingTrunkGraphRelation(true);
        }

        // 主要逻辑，生成静态调用路径失败，返回业务异常
        graphRelationService.initGenRelationToDatabase(array);
        return CommonResult.success(null, "success");
    }

    @PostMapping("/genTrunkAppRelations")
    @ApiOperation(value = "genTrunkAppRelations", notes = "单独生成Trunk的调用关系")
    public CommonResult<String> genTrunkAppRelations(@RequestParam("jar_path") String jarPath) {
        // jarPath 不能为空
        if (StrUtil.hasEmpty(jarPath)) {
            throw ServiceExceptionUtil.exception(JAR_PATH_NOT_EXIST);
        }

        // jarPath这个路径集合，必须都是有效的路径
        String[] array = GraphFileUtil.getJarArray(jarPath, FLAG_SPACE);
        if (array.length > 1 || !GraphFileUtil.isValidJarPath(array)) {
            throw ServiceExceptionUtil.exception(JAR_PATH_INVALID);
        }

        // 获取参数
        String[] split = jarPath.split(COMMON_SEPARATOR);
        int length = split.length;
        String appName = split[length - 4];

        // 相同App的Trunk不能同时生成调用关系
        synchronized (this) {
            if (singletonService.getIsGeneratingTrunkGraphRelation().contains(appName)) {
                throw ServiceExceptionUtil.exception(GEN_TRUNK_RELATION_ING);
            }
            singletonService.addIsGeneratingTrunkGraphRelation(appName);
        }

        graphRelationService.genTrunkRelationToDatabase(jarPath, appName);
        return CommonResult.success(null, "success");
    }

    @PostMapping("/genVersionAppRelations")
    @ApiOperation(value = "genVersionAppRelations", notes = "单独生成Version的调用关系")
    public CommonResult<String> genVersionAppRelations(@RequestParam("jar_path") String jarPath) {
        // jarPath 不能为空
        if (StrUtil.hasEmpty(jarPath)) {
            throw ServiceExceptionUtil.exception(JAR_PATH_NOT_EXIST);
        }

        // jarPath这个路径集合，必须都是有效的路径
        String[] array = GraphFileUtil.getJarArray(jarPath, FLAG_SPACE);
        if (array.length > 1 || !GraphFileUtil.isValidJarPath(array)) {
            throw ServiceExceptionUtil.exception(JAR_PATH_INVALID);
        }

        // 获取参数
        String[] split = jarPath.split(COMMON_SEPARATOR);
        int length = split.length;
        String appName = split[length - 4];
        String auditVersion = split[length - 3].substring(split[length - 3].indexOf(FLAG_SEP) + 1).replace(FLAG_SEP, FLAG_JAR) + FLAG_JAR + split[length - 2];

        // 判断是否正在生成关系
        synchronized (this) {
            if (singletonService.getIsGeneratingVersionGraphRelation().contains(appName)) {
                throw ServiceExceptionUtil.exception(GEN_VERSION_RELATION_ING);
            }
            singletonService.addIsGeneratingVersionGraphRelation(appName);
        }

        // 替换version的内容
        graphRelationService.genVersionRelationToDatabase(jarPath, appName, auditVersion);
        return CommonResult.success(null, "success");
    }

    @PostMapping("/getDownRelation")
    @ApiOperation(value = "getDownRelation", notes = "获取向下的调用关系")
    @Trace(operationName = "getDownRelation")
    @GraphMethodDoc("获取向下的调用关系")
    public CommonResult<MethodRelationVO> getDownRelation(@RequestBody @Valid MethodDownRelationInputVO inputVO) {
        log.info("获取向下的调用关系,begin");
        // 正在初始化的话，既不能查询也不能重新生成关系
        if (singletonService.getIsInitGeneratingTrunkGraphRelation()) {
            throw ServiceExceptionUtil.exception(INIT_JAR_RELATION_ING);
        }

        if (CollUtil.isNotEmpty(singletonService.getIsGeneratingTrunkGraphRelation())) {
            throw ServiceExceptionUtil.exception(GEN_TRUNK_RELATION_ING);
        }

        // 如果查询是在同一个APP内，则需要查看 是否在生成version的调用关系
        if (inputVO.getInApp() == 1 && inputVO.getAppType() == 1 && CollUtil.isNotEmpty(singletonService.getIsGeneratingVersionGraphRelation())) {
            throw ServiceExceptionUtil.exception(GEN_VERSION_RELATION_ING);
        }

        MethodRelationVO methodRelationVO = graphRelationService.getDownRelation(inputVO);
        log.info("获取向下的调用关系,end");
        return CommonResult.success(methodRelationVO, "success");
    }

    @PostMapping("/getUpRelation")
    @ApiOperation(value = "getUpRelation", notes = "获取向上的调用关系")
    public CommonResult<MethodUpRelationVO> getUpRelation(@RequestBody @Valid MethodUpRelationInputVO inputVO) {
        // 正在初始化的话，既不能查询也不能重新生成关系
        if (singletonService.getIsInitGeneratingTrunkGraphRelation()) {
            throw ServiceExceptionUtil.exception(INIT_JAR_RELATION_ING);
        }

        if (CollUtil.isNotEmpty(singletonService.getIsGeneratingTrunkGraphRelation())) {
            throw ServiceExceptionUtil.exception(GEN_TRUNK_RELATION_ING);
        }

        MethodUpRelationVO methodRelationVO = graphRelationService.getUpRelation(inputVO);
        return CommonResult.success(methodRelationVO, "success");
    }

    @GetMapping("/graphClearCache")
    @ApiOperation(value = "graphClearCache", notes = "graph清除本地缓存，非异常勿点！")
    public CommonResult<Object> graphClearCache(@RequestParam("name") String name,
                                                @RequestParam("type") @NotNull Integer type) {
        // 0，处理初始化缓存
        if (type == 0) {
            singletonService.setIsInitGeneratingTrunkGraphRelation(false);

        }
        // 1，处理trunk的缓存
        else if (type == 1) {
            singletonService.removeIsGeneratingTrunkGraphRelation(name);
        }
        // 2，处理version的缓存
        else if (type == 2) {
            singletonService.removeIsGeneratingVersionGraphRelation(name);
        }
        return CommonResult.success(null, "success");
    }

    @GetMapping("/graphGetCache")
    @ApiOperation(value = "graphGetCache", notes = "graph查看本地缓存，非异常勿点！")
    public CommonResult<Object> graphGetCache(@RequestParam("type") @NotNull Integer type) {
        // 0，处理初始化缓存
        if (type == 0) {
            return CommonResult.success(singletonService.getIsInitGeneratingTrunkGraphRelation(), "success");
        }
        // 1，处理trunk的缓存
        else if (type == 1) {
            return CommonResult.success(singletonService.getIsGeneratingTrunkGraphRelation(), "success");
        }
        // 2，处理version的缓存
        else if (type == 2) {
            return CommonResult.success(singletonService.getIsGeneratingVersionGraphRelation(), "success");
        }
        return CommonResult.success(null, "success");
    }

    @GetMapping("/getBranchList")
    @ApiOperation(value = "getBranchList", notes = "graph获取工程的分支列表，调用beetle")
    public CommonResultVO getBranchList(@RequestParam("app_name") @NotEmpty(message = "app_name") String appName) {
        CommonResultVO result = graphRelationService.getBranchList(appName);
        if (ObjectUtil.isEmpty(result)) {
            throw ServiceExceptionUtil.exception(GET_REMOTE_REQUEST_FAIL);
        }
        return result;
    }

    @GetMapping("/getBranchPackList")
    @ApiOperation(value = "getBranchPackList", notes = "graph获取分支软件列表，调用beetle")
    public CommonResultVO getBranchPackList(@RequestParam("branch_name") @NotEmpty(message = "branch_name") String branchName) {
        CommonResultVO result = graphRelationService.getBranchPackList(branchName);

        if (ObjectUtil.isEmpty(result)) {
            throw ServiceExceptionUtil.exception(GET_REMOTE_REQUEST_FAIL);
        }
        return result;
    }
}
