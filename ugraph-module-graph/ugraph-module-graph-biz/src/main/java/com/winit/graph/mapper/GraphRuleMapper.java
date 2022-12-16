package com.winit.graph.mapper;

import com.winit.graph.entity.dto.AppFilterRuleDTO;
import com.winit.graph.entity.dto.AppInfoDTO;
import com.winit.graph.entity.dto.ClassDetailDTO;
import com.winit.graph.entity.dto.MethodDetailDTO;
import com.winit.graph.entity.inputvo.AppFilterRuleSaveVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface GraphRuleMapper {

    void insertAppFilterRule(@Param("list") List<AppFilterRuleSaveVO> list);

    ArrayList<String> selectRuleByAppName(@Param("app_name") String appName);

    List<AppFilterRuleDTO> selectAppFilterRule(@Param("item") AppFilterRuleDTO appFilterRuleDTO);

    Integer selectAppFilterRuleCount(@Param("item") AppFilterRuleDTO queryParam);

    void patchDeleteAppFilterRule(@Param("appName") String appName);

    List<AppInfoDTO> selectAppInfo(@Param("item") AppInfoDTO queryParam);

    List<AppInfoDTO> selectAppInfoVersion(@Param("item") AppInfoDTO queryParam);

    List<AppInfoDTO> selectAppVersionInfo(@Param("item") AppInfoDTO queryParam);

    List<MethodDetailDTO> selectTrunkMethodDetailInClass(@Param("item") MethodDetailDTO queryParam);

    ClassDetailDTO selectTrunkClassDetail(@Param("item") ClassDetailDTO queryParam);

    List<Integer> selectTrunkAppNumByAppName(@Param("item") AppInfoDTO appInfoDTO);

    List<ClassDetailDTO> selectTrunkClassDetailInApp(@Param("item") ClassDetailDTO queryParam);

    Integer selectAppInfoCountByAppName(@Param("appName") String appName);

    Integer selectTrunkAppNumByAppNameAndVersion(@Param("appName") String appName);

    Integer selectVersionAppNumByAppNameAndVersion(@Param("appName") String appName, @Param("version") String version);

    List<String> selectTrunkClassHashByAppNum(@Param("appNum") Integer appNum);

    List<String> selectVersionClassHashByAppNum(@Param("appNum") Integer appNum);

    List<String> selectTrunkMethodByAppNum(@Param("appNum") Integer appNum);

    List<String> selectVersionMethodByAppNum(@Param("appNum") Integer versionAppNum);

    List<String> selectTrunkDownMethodCallByAppNumAndMethodHash(@Param("appNum") Integer trunkAppNum, @Param("methodHash") String methodHash);

    List<String> selectVersionDownMethodCallByAppNumAndMethodHash(@Param("appNum") Integer trunkAppNum, @Param("methodHash") String methodHash);

    List<String> selectVersionUpMethodCallByAppNumAndMethodHash(@Param("appNum") Integer trunkAppNum, @Param("methodHash") String methodHash);

    void updateVersionClassStatus(@Param("list") List<String> versionClassList, @Param("status") Integer status);

    void updateVersionMethodStatus(@Param("list") List<String> versionClassList, @Param("status") Integer status);

    List<String> selectVersionClassHashByMethodHashList(@Param("list") List<String> upVersionMethodList);

    MethodDetailDTO selectVersionMethodDetailByMethodHashAndAppNum(@Param("methodHash") String methodHash, @Param("appNum") Integer versionAppNum);

    ClassDetailDTO selectVersionClassDetail(@Param("item") ClassDetailDTO queryParam);

    List<ClassDetailDTO> selectVersionClassDetailInApp(@Param("item") ClassDetailDTO queryParam);

    List<MethodDetailDTO> selectVersionMethodDetailInClass(@Param("item") MethodDetailDTO queryParam);

    List<Integer> selectVersionAppNumByAppName(@Param("appName") String appName);
}
