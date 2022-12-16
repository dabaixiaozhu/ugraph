package com.winit.graph.service;

import com.winit.graph.entity.inputvo.MethodDownRelationInputVO;
import com.winit.graph.entity.inputvo.MethodUpRelationInputVO;
import com.winit.graph.entity.vo.CommonResultVO;
import com.winit.graph.entity.vo.MethodRelationVO;
import com.winit.graph.entity.vo.MethodUpRelationVO;

public interface GraphRelationService {

    /**
     * 生成调用关系，并入库
     */
    void initGenRelationToDatabase(String[] array);

    /**
     * 获取当前方法的向下调用路径
     */
    MethodRelationVO getDownRelation(MethodDownRelationInputVO inputVO);

    /**
     * 获取当前方法的向上调用路径
     */
    MethodUpRelationVO getUpRelation(MethodUpRelationInputVO inputVO);

    /**
     * 替换Trunk的内容
     */
    void genTrunkRelationToDatabase(String jarPath, String appName);

    /**
     * 替换Version的内容
     */
    void genVersionRelationToDatabase(String jarPath, String appName, String auditVersion);

    /**
     * 获取工程分支列表
     */
    CommonResultVO getBranchList(String appName);

    /**
     * 获取分支软件列表
     */
    CommonResultVO getBranchPackList(String branchName);
}
