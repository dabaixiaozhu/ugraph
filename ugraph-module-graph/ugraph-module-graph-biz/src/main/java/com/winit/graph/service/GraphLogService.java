package com.winit.graph.service;

import com.github.pagehelper.PageInfo;
import com.winit.graph.entity.dto.AppOperateLogDTO;
import com.winit.graph.entity.inputvo.GraphLogInputVO;
import com.winit.graph.entity.vo.AppOperateLogVO;

public interface GraphLogService {

    /**
     * 存储日志
     */
    Long saveAuditLog(AppOperateLogDTO appOperateLogDTO);

    /**
     * 查看日志
     */
    PageInfo<AppOperateLogVO> selectAppOperateLog(GraphLogInputVO inputVO);

    /**
     * 更新version
     */
    void updateAuditLogToOldVersion(Long id);
}
