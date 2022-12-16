package com.winit.graph.controller;


import com.winit.graph.common.entity.CommonResult;
import com.winit.graph.common.exception.util.ServiceExceptionUtil;
import com.winit.graph.entity.inputvo.AppFilterRuleInputVO;
import com.winit.graph.entity.vo.AppFilterRuleVO;
import com.winit.graph.entity.vo.AppInfoVO;
import com.winit.graph.entity.vo.ClassDetailVO;
import com.winit.graph.entity.vo.MethodDetailVO;
import com.winit.graph.service.GraphRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.winit.graph.common.exception.enums.GraphErrorCodeConstants.APP_NOT_EXIT;

@RestController
@Validated
@Api(tags = "用于获取Graph的规则相关的内容")
@RequestMapping("/graphrule")
public class GraphRuleController {

    @Autowired
    private GraphRuleService graphRuleService;

    @GetMapping("/getAppTrunkInfo")
    @ApiOperation(value = "getAppTrunkInfo", notes = "获取应用Trunk详情")
    public CommonResult<List<AppInfoVO>> getAppTrunkInfo() {
        List<AppInfoVO> result = graphRuleService.getAppTrunkInfo();
        return CommonResult.success(result, "success");
    }

    @GetMapping("/getAppClass")
    @ApiOperation(value = "getAppClass", notes = "获取当前App下的Class")
    public CommonResult<List<ClassDetailVO>> getAppClass(@RequestParam(required = false, name = "filter_name") String filterName,
                                                         @RequestParam("app_num") @NotNull Integer appNum,
                                                         @RequestParam("type") @NotNull Integer type) {
        List<ClassDetailVO> result = graphRuleService.getAppClass(appNum, filterName, type);
        return CommonResult.success(result, "success");
    }

    @GetMapping("/getClassInfo")
    @ApiOperation(value = "getClassInfo", notes = "获取类详情")
    public CommonResult<ClassDetailVO> getClassInfo(@RequestParam("class_hash") @NotEmpty(message = "class_hash") String classHash,
                                                    @RequestParam("app_num") @NotNull Integer appNum,
                                                    @RequestParam("type") @NotNull Integer type) {
        ClassDetailVO classDetailVO = graphRuleService.getClassDetail(classHash, appNum, type);
        return CommonResult.success(classDetailVO, "success");
    }

    @PostMapping("/saveAppFilterRule")
    @ApiOperation(value = "saveAppFilterRule", notes = "添加应用过滤规则")
    public CommonResult<AppFilterRuleVO> saveAppFilterRule(@RequestBody @Valid AppFilterRuleInputVO inputVO) {
        // 重复添加的异常
        if (!graphRuleService.saveAppFilterRule(inputVO)) {
            throw ServiceExceptionUtil.exception(APP_NOT_EXIT);
        }
        return CommonResult.success(null, "success");
    }

    @GetMapping("/getAppFilterRule")
    @ApiOperation(value = "getAppFilterRule", notes = "获取应用过滤规则")
    public CommonResult<List<AppFilterRuleVO>> getAppFilterRule(@RequestParam("app_name") @NotEmpty(message = "app_name缺失") String appName) {
        List<AppFilterRuleVO> appFilterRuleVO = graphRuleService.getAppFilterRule(appName);
        return CommonResult.success(appFilterRuleVO, "success");
    }

    @GetMapping("/getMethodInClass")
    @ApiOperation(value = "getMethodInClass", notes = "获取当前类下所有的方法")
    public CommonResult<List<MethodDetailVO>> getMethodInClass(@RequestParam("class_hash") @NotEmpty(message = "class_hash缺失") String classHash,
                                                               @RequestParam("app_num") @NotNull Integer appNum,
                                                               @RequestParam("type") @NotNull Integer type) {
        List<MethodDetailVO> methodDetailVOList = graphRuleService.getMethodDetailInClass(classHash, appNum, type);
        return CommonResult.success(methodDetailVOList, "success");
    }

    @GetMapping("/getAppVersionInfo")
    @ApiOperation(value = "getAppVersionInfo", notes = "获取应用Version详情")
    public CommonResult<List<AppInfoVO>> getAppVersionInfo(@RequestParam("app_name") @NotEmpty(message = "app_name缺失") String appName) {
        List<AppInfoVO> result = graphRuleService.getAppVersionInfo(appName);
        return CommonResult.success(result, "success");
    }
}
