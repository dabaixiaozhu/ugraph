package com.winit.graph.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.winit.graph.entity.dto.AppFilterRuleDTO;
import com.winit.graph.entity.dto.AppInfoDTO;
import com.winit.graph.entity.dto.ClassDetailDTO;
import com.winit.graph.entity.dto.MethodDetailDTO;
import com.winit.graph.entity.inputvo.AppFilterRuleInputVO;
import com.winit.graph.entity.inputvo.AppFilterRuleSaveVO;
import com.winit.graph.entity.vo.AppFilterRuleVO;
import com.winit.graph.entity.vo.AppInfoVO;
import com.winit.graph.entity.vo.ClassDetailVO;
import com.winit.graph.entity.vo.MethodDetailVO;
import com.winit.graph.mapper.GraphRelationMapper;
import com.winit.graph.mapper.GraphRuleMapper;
import com.winit.graph.service.GraphRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.winit.graph.common.entity.JavaCGConstants.TRUNK_NAME;

@Service
public class GraphRuleServiceImpl implements GraphRuleService {

    @Autowired
    private GraphRuleMapper graphRuleMapper;

    @Autowired
    private GraphRelationMapper graphRelationMapper;

    @Override
    public boolean saveAppFilterRule(AppFilterRuleInputVO inputVO) {
        // 检查一下当前的appName是否在库中
        if (graphRuleMapper.selectAppInfoCountByAppName(inputVO.getAppName()) <= 0) {
            return false;
        }

        // 将此工程之前的过滤规则都删除
        graphRuleMapper.patchDeleteAppFilterRule(inputVO.getAppName());

        // 重新添加此工程的过滤规则
        List<AppFilterRuleSaveVO> filterList = inputVO.getFilterList();
        if (CollUtil.isNotEmpty(filterList)) {
            filterList.forEach(filterRule -> filterRule.setAppName(inputVO.getAppName()));
            graphRuleMapper.insertAppFilterRule(filterList);
        }

        return true;
    }

    @Override
    public List<AppFilterRuleVO> getAppFilterRule(String appName) {
        AppFilterRuleDTO queryParam = new AppFilterRuleDTO();
        queryParam.setAppName(appName);
        List<AppFilterRuleDTO> appFilterRuleDTOList = graphRuleMapper.selectAppFilterRule(queryParam);

        List<AppFilterRuleVO> result = new ArrayList<>();
        for (AppFilterRuleDTO appFilterRuleDTO : appFilterRuleDTOList) {
            result.add(Convert.convert(AppFilterRuleVO.class, appFilterRuleDTO));
        }
        // 类型转换
        return result;
    }

    @Override
    public List<AppInfoVO> getAppTrunkInfo() {
        AppInfoDTO queryParam = new AppInfoDTO();
        queryParam.setAppVersion(TRUNK_NAME);
        List<AppInfoDTO> appInfoVOList = graphRuleMapper.selectAppInfo(queryParam);

        List<AppInfoVO> result = new ArrayList<>();
        for (AppInfoDTO appInfoVO : appInfoVOList) {
            result.add(Convert.convert(AppInfoVO.class, appInfoVO));
        }
        return result;
    }

    @Override
    public List<AppInfoVO> getAppVersionInfo(String appName) {
        AppInfoDTO queryParam = new AppInfoDTO();
        queryParam.setAppName(appName);
        List<AppInfoDTO> appInfoDTOList = graphRuleMapper.selectAppInfoVersion(queryParam);

        List<AppInfoVO> result = new ArrayList<>();
        for (AppInfoDTO appInfoDTO : appInfoDTOList) {
            result.add(Convert.convert(AppInfoVO.class, appInfoDTO));
        }
        return result;
    }

    @Override
    public List<MethodDetailVO> getMethodDetailInClass(String classHash, Integer appNum, Integer type) {
        MethodDetailDTO queryParam = new MethodDetailDTO();
        queryParam.setClassHash(classHash);
        queryParam.setAppNum(appNum);

        List<MethodDetailDTO> methodDetailDTOList;
        // Trunk
        if (type ==0) {
            methodDetailDTOList = graphRuleMapper.selectTrunkMethodDetailInClass(queryParam);
        }
        // Version
        else {
            methodDetailDTOList = graphRuleMapper.selectVersionMethodDetailInClass(queryParam);
        }

        List<MethodDetailVO> result = new ArrayList<>();
        for (MethodDetailDTO methodDetailDTO : methodDetailDTOList) {
            result.add(Convert.convert(MethodDetailVO.class, methodDetailDTO));
        }

        return result;
    }

    @Override
    public ClassDetailVO getClassDetail(String classHash, Integer appNum, Integer type) {
        ClassDetailDTO queryParam = new ClassDetailDTO();
        queryParam.setClassHash(classHash);
        queryParam.setAppNum(appNum);
        // trunk
        if (type == 0) {
            return Convert.convert(ClassDetailVO.class, graphRuleMapper.selectTrunkClassDetail(queryParam));
        }
        // version
        return Convert.convert(ClassDetailVO.class, graphRuleMapper.selectVersionClassDetail(queryParam));
    }

    @Override
    public List<ClassDetailVO> getAppClass(Integer appNum, String filterName, Integer type) {
        // 忽略大小写查询
        ClassDetailDTO queryParam = new ClassDetailDTO();
        if (StrUtil.isNotBlank(filterName)) {
            queryParam.setClassFullName(filterName.toUpperCase());
        }

        List<ClassDetailDTO> classDetailDTOList;
        queryParam.setAppNum(appNum);
        // 表示Trunk
        if (type == 0) {
            classDetailDTOList = graphRuleMapper.selectTrunkClassDetailInApp(queryParam);
        } else {
            classDetailDTOList = graphRuleMapper.selectVersionClassDetailInApp(queryParam);
        }

        List<ClassDetailVO> result = new ArrayList<>();
        for (ClassDetailDTO classDetailDTO : classDetailDTOList) {
            result.add(Convert.convert(ClassDetailVO.class, classDetailDTO));
        }
        return result;
    }
}
