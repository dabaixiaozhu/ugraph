package com.winit.graph.service;

import com.winit.graph.entity.inputvo.AppFilterRuleInputVO;
import com.winit.graph.entity.vo.AppFilterRuleVO;
import com.winit.graph.entity.vo.AppInfoVO;
import com.winit.graph.entity.vo.ClassDetailVO;
import com.winit.graph.entity.vo.MethodDetailVO;

import java.util.List;

public interface GraphRuleService {

    /**
     * 添加App的过滤规则
     */
    boolean saveAppFilterRule(AppFilterRuleInputVO inputVO);

    /**
     * 获取App的过滤规则
     */
    List<AppFilterRuleVO> getAppFilterRule(String appName);

    /**
     * 获取所有Trunk的App信息
     */
    List<AppInfoVO> getAppTrunkInfo();

    /**
     * 获取对应Version的App信息
     */
    List<AppInfoVO> getAppVersionInfo(String appName);

    /**
     * 获取当前App下此类中所有的方法
     */
    List<MethodDetailVO> getMethodDetailInClass(String classHash, Integer appNum, Integer type);

    /**
     * 获取类详情
     */
    ClassDetailVO getClassDetail(String classHash, Integer appNum, Integer type);

    /**
     * 获取当前App下的Class
     */
    List<ClassDetailVO> getAppClass(Integer appNum, String filterName, Integer type);

}
