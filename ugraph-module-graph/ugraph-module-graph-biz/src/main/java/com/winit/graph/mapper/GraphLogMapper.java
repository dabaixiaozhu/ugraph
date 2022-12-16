package com.winit.graph.mapper;

import com.winit.graph.entity.dto.AppOperateLogDTO;
import com.winit.graph.entity.inputvo.GraphLogInputVO;
import com.winit.graph.entity.vo.AppOperateLogVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GraphLogMapper {

    Integer insertAppOperateLog(@Param("item") AppOperateLogDTO appOperateLogDTO);

    List<AppOperateLogVO> selectAppOperateLogByPage(@Param("item") GraphLogInputVO inputVO);

    void updateAppOperateLogById(@Param("id") Long id);
}
