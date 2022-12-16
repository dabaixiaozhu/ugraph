package com.winit.graph.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "告警相关的controller")
@RequestMapping("/alarm")
public class AlarmController {

    //每次调用睡眠1.5秒，模拟超时的报警
    @GetMapping("/timeout")
    @ApiOperation(value = "timeout", notes = "模拟超时，多次调用之后就可以生成告警信息。")
    public String timeout() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "timeout";
    }
}
