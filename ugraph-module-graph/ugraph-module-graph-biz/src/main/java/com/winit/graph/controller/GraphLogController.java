package com.winit.graph.controller;

import com.github.pagehelper.PageInfo;
import com.winit.graph.annotation.GraphMethodDoc;
import com.winit.graph.common.entity.CommonResult;
import com.winit.graph.entity.inputvo.GraphLogInputVO;
import com.winit.graph.entity.vo.AppOperateLogVO;
import com.winit.graph.service.GraphLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
@Api(tags = "用于获取Graph的日志相关的内容")
@RequestMapping("/graphlog")
public class GraphLogController {
    private static final Logger log = LoggerFactory.getLogger(GraphLogController.class);

    @Autowired
    private GraphLogService graphLogService;

    @PostMapping("/getAppOperateLog")
    @ApiOperation(value = "getAppOperateLog", notes = "获取日志内容")
    @GraphMethodDoc("测试自定义注解 lin")
    public CommonResult<PageInfo<AppOperateLogVO>> getAppOperateLog(@RequestBody @Valid GraphLogInputVO inputVO) {
        PageInfo<AppOperateLogVO> result = graphLogService.selectAppOperateLog(inputVO);
        return CommonResult.success(result, "success");
    }
}
