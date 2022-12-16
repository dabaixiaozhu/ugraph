package com.winit.graph.mapper;

import com.winit.graph.entity.dto.ClassDetailDTO;
import com.winit.graph.entity.dto.MethodRelationDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GraphRelationMapper {

    int patchDropFormerTable(@Param("list") List<String> tableNameList);

    int createGraphClassCallTable(@Param("table_name") String tableName);

    int createGraphJarInfoTable(@Param("table_name") String tableName);

    int createGraphMethodCallTable(@Param("table_name") String tableName);

    int createGraphMethodDetailTable(@Param("table_name") String tableName);

    List<MethodRelationDTO> selectDownRelationByMethodHash(@Param("method_hash") String methodHash);

    List<MethodRelationDTO> selectVersionDownRelationByMethodHash(@Param("method_hash") String methodHash, @Param("app_num") Integer appNum);

    List<MethodRelationDTO> selectUpRelationByMethodHash(@Param("method_hash") String methodHash);

    MethodRelationDTO selectMethodDetailByMethodHash(@Param("method_hash") String methodHash);

    MethodRelationDTO selectVersionMethodDetailByMethodHash(@Param("method_hash") String iMethodHash, @Param("app_num") Integer appNum);

    int createGraphClassDetailTable(@Param("table_name") String tableName);

    Integer selectMethodDetailCount(@Param("item") ClassDetailDTO queryParam);

    int deleteAppInfoByAppNum(@Param("list") List<Integer> appNumList);

    int deleteMethodDetailByAppNum(@Param("list") List<Integer> appNumList);

    int deleteClassCallByAppNum(@Param("list") List<Integer> appNumList);

    int deleteVersionClassCallByAppNum(@Param("list") List<Integer> appNumList);

    int deleteClassDetailByAppNum(@Param("list") List<Integer> appNumList);

    int deleteVersionClassDetailByAppNum(@Param("list") List<Integer> appNumList);

    int deleteMethodCallByAppNum(@Param("list") List<Integer> appNumList);

    int deleteVersionAppInfoByAppNum(@Param("list") List<Integer> appNumList);

    int deleteVersionMethodCallByAppNum(@Param("list") List<Integer> appNumList);

    int deleteVersionMethodDetailByAppNum(@Param("list") List<Integer> appNumList);

    List<String> selectClassNameInInterface(@Param("class_hash") String classHash);

    List<String> selectVersionClassNameInInterface(@Param("class_hash") String classHash, @Param("app_num") Integer appNum);

    String selectClassNameInExtendsFather(@Param("class_hash") String classHash);

    String selectVersionClassNameInExtendsFather(@Param("class_hash") String classHash, @Param("app_num") Integer appNum);

    Integer selectClassAppNumByClassHash(@Param("class_hash") String classHash);

    Integer selectVersionClassAppNumByClassHash(@Param("class_hash") String classHash, @Param("app_num") Integer appNum);

    MethodRelationDTO selectCalleeMethodInfoByMethodHash(@Param("method_hash") String methodHash);

    MethodRelationDTO selectVersionCalleeMethodInfoByMethodHash(@Param("method_hash") String methodHash, @Param("app_num") Integer appNum);

    List<String> selectClassNameInInterfaceImpl(@Param("class_hash") String classHash);

    List<String> selectClassNameInExtendsSon(@Param("class_hash") String classHash);

    Integer selectVersionAppNum(@Param("app_name") String appName, @Param("app_version") String appVersion);

    String selectItfCalleeClassNameByCallerClassHash(@Param("class_hash") String classHash);
}
