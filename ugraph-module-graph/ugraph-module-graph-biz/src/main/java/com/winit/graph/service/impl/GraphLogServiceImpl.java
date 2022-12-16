package com.winit.graph.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.winit.graph.entity.dto.AppOperateLogDTO;
import com.winit.graph.entity.inputvo.GraphLogInputVO;
import com.winit.graph.entity.vo.AppOperateLogVO;
import com.winit.graph.mapper.GraphLogMapper;
import com.winit.graph.service.GraphLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 记录操作日志
 */
@Service
public class GraphLogServiceImpl implements GraphLogService {

    @Autowired
    private GraphLogMapper graphLogMapper;

    @Override
    public Long saveAuditLog(AppOperateLogDTO appOperateLogDTO) {
        graphLogMapper.insertAppOperateLog(appOperateLogDTO);
        return appOperateLogDTO.getId();
    }

    @Override
    public PageInfo<AppOperateLogVO> selectAppOperateLog(GraphLogInputVO inputVO) {
        PageHelper.startPage(inputVO.getPageNum(), inputVO.getPageSize());
        List<AppOperateLogVO> result = graphLogMapper.selectAppOperateLogByPage(inputVO);
        return new PageInfo<>(result);
    }

    @Override
    public void updateAuditLogToOldVersion(Long id) {
        graphLogMapper.updateAppOperateLogById(id);
    }
}
