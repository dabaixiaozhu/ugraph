package com.winit.graphgen.mapper;

import com.winit.graphgen.entity.dto.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public interface GraphGenMapper {

    void patchInsertMethodCall(@Param("list") List<MethodCallDto> methodCallList);

    void patchInsertMethodDetail(@Param("list") List<MethodDetailDto> insertMethodDetailList);

    Integer insertAppInfo(@Param("item") AppInfoDto appInfoDto);

    Integer insertAppInfoVersion(@Param("item") AppInfoDto appInfoDto);

    void patchInsertClassCall(@Param("list") List<ClassCallDto> insertClassCallDtoList);

    void patchInsertClassDetail(@Param("list") List<ClassDetailDto> insertClassDetailDtoList);

    void patchInsertMethodDetailVersion(@Param("list") List<MethodDetailDto> insertMethodDetailList);

    void patchInsertMethodCallVersion(@Param("list") List<MethodCallDto> versionMethodCallList);

    void patchInsertClassCallVersion(@Param("list") List<ClassCallDto> insertClassCallDtoList);

    void patchInsertClassDetailVersion(@Param("list") List<ClassDetailDto> insertClassDetailDtoList);
}
