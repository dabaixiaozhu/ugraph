package com.winit.graph.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.winit.graph.common.entity.CallTypeEnum;
import com.winit.graph.common.entity.JavaCGConstants;
import com.winit.graph.common.util.GraphJACGUtil;
import com.winit.graph.entity.dto.*;
import com.winit.graph.entity.inputvo.MethodDownRelationInputVO;
import com.winit.graph.entity.inputvo.MethodUpRelationInputVO;
import com.winit.graph.entity.vo.CommonResultVO;
import com.winit.graph.entity.vo.MethodRelationVO;
import com.winit.graph.entity.vo.MethodUpRelationVO;
import com.winit.graph.mapper.GraphRelationMapper;
import com.winit.graph.mapper.GraphRuleMapper;
import com.winit.graph.service.GraphLogService;
import com.winit.graph.service.GraphRelationService;
import com.winit.graph.service.SingletonService;
import com.winit.graphgen.service.GraphGenService;
import org.apache.skywalking.apm.toolkit.trace.Tag;
import org.apache.skywalking.apm.toolkit.trace.Tags;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.winit.graph.common.entity.JavaCGConstants.*;


@Service
public class GraphRelationServiceImpl implements GraphRelationService {

    private static final Logger log = LoggerFactory.getLogger(GraphRelationServiceImpl.class);

    @Resource
    private GraphRelationMapper graphRelationMapper;

    @Resource
    private GraphGenService graphGenService;

    @Autowired
    private GraphRuleMapper graphRuleMapper;

    @Autowired
    private SingletonService singletonService;

    @Autowired
    private GraphLogService graphLogService;

    @Resource(name = "defaultThreadPool")
    ThreadPoolTaskExecutor threadPoolExecutor;

    @Value("${graph.file.path}")
    private String rootPath;

    @Value("${beetle.get.versionpath}")
    private String versionPath;

    @Value("${beetle.get.versionjarpath}")
    private String versionJarPath;

    @Override
    public void initGenRelationToDatabase(String[] array) {
        try {
            long startTime = System.currentTimeMillis();
            // drop旧表，重新生成的数据会进行覆盖
            if (!dropFormerTable()) {
                log.error("drop表出现异常");
                return;
            }

            // 新建数据库表
            if (!createTables()) {
                log.error("新建数据库表出现异常");
                return;
            }

            List<CompletableFuture<Boolean>> futures = new ArrayList<>();
            for (String jarPath : array) {
                // 获取参数
                String[] split = jarPath.split(COMMON_SEPARATOR);
                int length = split.length;
                String appName = split[length - 4];

                // 记录init日志
                Long upId = graphLogService.saveAuditLog(new AppOperateLogDTO.Builder().setAppName(appName).setAppVersion(INIT_TAG).setStatus(0).setVersion(1).build());

                // 多线程执行任务
                futures.add(CompletableFuture.supplyAsync(() -> {
                            log.info("init开始处理jar包：" + jarPath);
                            String basePath = rootPath + INIT_PATH + File.separator + appName;

                            try {
                                // 复制文件到固定的文件夹
                                FileInputStream inputStream = new FileInputStream(jarPath);
                                Path path = Paths.get(basePath);
                                Files.createDirectories(path);

                                String trunkOutPath = basePath + File.separator + appName + jarPath.substring(jarPath.lastIndexOf(FLAG_DOT));
                                FileOutputStream outputStream = new FileOutputStream(trunkOutPath);
                                IoUtil.copy(inputStream, outputStream);

                                // 解压压缩的文件，到当前文件夹
                                ZipUtil.unzip(trunkOutPath);
                            } catch (IOException e) {
                                log.error("init复制移动文件发生异常:{}", e.getMessage());
                                return false;
                            }

                            // 读取jar包内容，写入txt文件
                            if (!graphGenService.readJarWriteToFile(jarPath, basePath, appName, TRUNK_FLAG, TRUNK_NAME)) {
                                log.error("init读取jar包内容，出现异常");
                                return false;
                            }

                            // 读取 txt文件内容，写入数据库
                            String txtFilePath = basePath + File.separator + appName + JavaCGConstants.EXT_TXT;
                            if (!graphGenService.readFileWriteToDb(txtFilePath, TRUNK_FLAG)) {
                                log.error("读取 txt文件内容，写入数据库，出现异常");
                                return false;
                            }

                            // 删除文件夹，以及文件夹以下的内容
                            if (!FileUtil.del(basePath)) {
                                log.error("初始化删除文件，出现异常");
                                return false;
                            }

                            log.info("init结束处理jar包：" + jarPath);
                            return true;
                        }, threadPoolExecutor)
                        .whenComplete((result, error) -> {
                            // 只要结束了，都需要修改日志状态
                            graphLogService.updateAuditLogToOldVersion(upId);

                            if (result) {
                                // 记录成功日志
                                log.info("记录成功异步生成version内容日志：" + appName);
                                graphLogService.saveAuditLog(new AppOperateLogDTO.Builder().setAppName(appName).setAppVersion(INIT_TAG).setStatus(1).setVersion(1).build());
                            } else {
                                // 记录异常日志
                                log.error("记录失败异步生成version内容日志：" + appName);
                                graphLogService.saveAuditLog(new AppOperateLogDTO.Builder().setAppName(appName).setAppVersion(INIT_TAG).setStatus(2).setVersion(1).build());
                            }
                        }));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            // 表示此appName已生成完成
            singletonService.setIsInitGeneratingTrunkGraphRelation(false);

            long spendTime = System.currentTimeMillis() - startTime;
            log.info("{} 耗时: {} s", this.getClass().getSimpleName(), spendTime / 1000.0D);
        } catch (Exception e) {
            log.error("初始化生成调用链路出现异常：{}", e.getMessage());
        }
    }

    @Override
    public void genTrunkRelationToDatabase(String jarPath, String appName) {
        // 删除原表的内容
        try {
            log.info("开始生成trunk内容：" + appName);
            // 记录日志表，开始生成关系，只记录，不影响业务逻辑
            Long upId = graphLogService.saveAuditLog(new AppOperateLogDTO.Builder().setAppName(appName).setAppVersion(TRUNK_NAME).setStatus(0).setVersion(1).build());

            // 异步处理
            CompletableFuture<Boolean> cf = CompletableFuture
                    .supplyAsync(() -> {
                        log.info("开始异步生成trunk内容：" + appName);

                        long startTime = System.currentTimeMillis();
                        String basePath;
                        String trunkOutPath;
                        try {
                            // 复制文件到固定的文件夹
                            FileInputStream inputStream = new FileInputStream(jarPath);
                            basePath = rootPath + TRUNK_PATH + File.separator + appName;
                            Path path = Paths.get(basePath);
                            Files.createDirectories(path);

                            trunkOutPath = basePath + File.separator + appName + jarPath.substring(jarPath.lastIndexOf(FLAG_DOT));
                            FileOutputStream outputStream = new FileOutputStream(trunkOutPath);
                            IoUtil.copy(inputStream, outputStream);
                        } catch (IOException e) {
                            log.error("trunk复制移动文件发生异常:{}", e.getMessage());
                            return false;
                        }

                        // 解压压缩的文件，到当前文件夹
                        ZipUtil.unzip(trunkOutPath);
                        log.info("{} 复制文件耗时: {} s", this.getClass().getSimpleName(), (System.currentTimeMillis() - startTime) / 1000.0D);

                        // 删除之前的内容
                        AppInfoDTO appInfoDTO = new AppInfoDTO();
                        appInfoDTO.setAppName(appName);
                        List<Integer> appInfoDTOList = graphRuleMapper.selectTrunkAppNumByAppName(appInfoDTO);
                        // 之前插入过，才需要删除
                        if (CollUtil.isNotEmpty(appInfoDTOList)) {
                            if (!deleteTrunkInfo(appInfoDTOList)) {
                                log.error("trunk删除表数据出现异常");
                                return false;
                            }
                        }

                        // 读取jar包内容，写入txt文件
                        if (!graphGenService.readJarWriteToFile(jarPath, basePath, appName, TRUNK_FLAG, TRUNK_NAME)) {
                            log.error("trunk读取jar包内容，出现异常");
                            return false;
                        }
                        log.info("{} 写txt文件耗时: {} s", this.getClass().getSimpleName(), (System.currentTimeMillis() - startTime) / 1000.0D);

                        // 读取 txt文件内容，写入数据库
                        String txtFilePath = basePath + File.separator + appName + JavaCGConstants.EXT_TXT;
                        if (!graphGenService.readFileWriteToDb(txtFilePath, TRUNK_FLAG)) {
                            log.error("trunk读取 txt文件内容，写入数据库，出现异常");
                            return false;
                        }
                        log.info("{} 读取txt文件耗时: {} s", this.getClass().getSimpleName(), (System.currentTimeMillis() - startTime) / 1000.0D);

                        // 删除文件夹，以及文件夹以下的内容
                        if (!FileUtil.del(basePath)) {
                            log.error("trunk删除文件，出现异常");
                            return false;
                        }

                        // 先查询对应version的appNum
                        List<Integer> versionAppNumList = graphRuleMapper.selectVersionAppNumByAppName(appName);
                        // 批量删一下对应version表数据
                        if (CollUtil.isNotEmpty(versionAppNumList) && !deleteVersionInfo(versionAppNumList)) {
                            log.error("trunk删除version表数据，出现异常");
                            return false;
                        }

                        log.info("结束异步生成trunk内容：" + appName);
                        log.info("{} 总耗时: {} s", this.getClass().getSimpleName(), (System.currentTimeMillis() - startTime) / 1000.0D);
                        return true;
                    }, threadPoolExecutor)
                    .whenComplete((result, error) -> {
                        // 只要结束了，都需要修改状态
                        graphLogService.updateAuditLogToOldVersion(upId);
                        // 表示此appName已生成完成
                        singletonService.removeIsGeneratingTrunkGraphRelation(appName);
                        if (result) {
                            // 记录成功日志
                            log.info("记录成功异步生成version内容日志：" + appName);
                            graphLogService.saveAuditLog(new AppOperateLogDTO.Builder().setAppName(appName).setAppVersion(TRUNK_NAME).setStatus(1).setVersion(1).build());
                        } else {
                            // 记录异常日志
                            log.error("记录失败异步生成version内容日志：" + appName);
                            graphLogService.saveAuditLog(new AppOperateLogDTO.Builder().setAppName(appName).setAppVersion(TRUNK_NAME).setStatus(2).setVersion(1).build());
                        }
                    });

            log.info("结束生成trunk内容：" + appName);
        } catch (Exception e) {
            log.error("生成trunk调用链路出现异常：{}", e.getMessage());
        }
    }

    @Override
    public void genVersionRelationToDatabase(String jarPath, String appName, String auditVersion) {
        try {
            log.info("开始生成version版本的内容");
            // 记录日志表，开始生成关系，只记录，不影响业务逻辑
            Long upId = graphLogService.saveAuditLog(new AppOperateLogDTO.Builder().setAppName(appName).setAppVersion(auditVersion).setStatus(0).setVersion(1).build());

            // 异步处理
            CompletableFuture<Boolean> cf = CompletableFuture
                    .supplyAsync(() -> {
                        log.info("开始异步生成version内容：" + appName);
                        long startTime = System.currentTimeMillis();
                        // 复制文件到固定的文件夹
                        String basePath;
                        try {
                            FileInputStream inputStream = new FileInputStream(jarPath);
                            basePath = rootPath + VERSION_PATH + File.separator + appName;
                            Path path = Paths.get(basePath);
                            Files.createDirectories(path);

                            String versionOutPath = basePath + File.separator + appName + jarPath.substring(jarPath.lastIndexOf(FLAG_DOT));
                            FileOutputStream outputStream = new FileOutputStream(versionOutPath);
                            IoUtil.copy(inputStream, outputStream);

                            // 解压压缩的文件，到当前文件夹
                            ZipUtil.unzip(versionOutPath);
                        } catch (IOException e) {
                            log.error("version复制移动文件发生异常:{}", e.getMessage());
                            return false;
                        }

                        AppInfoDTO queryParam = new AppInfoDTO();
                        queryParam.setAppName(appName);
                        List<AppInfoDTO> appInfoVOList = graphRuleMapper.selectAppVersionInfo(queryParam);
                        // 确定要删除的旧版本内容
                        List<Integer> deleteNumList = getDeleteNumList(appInfoVOList, auditVersion);
                        if (CollUtil.isNotEmpty(deleteNumList) && !deleteVersionInfo(deleteNumList)) {
                            log.error("version删除表数据出现异常");
                            return false;
                        }

                        // 读取jar包内容，写入txt文件
                        if (!graphGenService.readJarWriteToFile(jarPath, basePath, appName, VERSION_FLAG, auditVersion)) {
                            log.error("version读取jar包内容，出现异常");
                            return false;
                        }

                        // 读取 txt文件内容，写入数据库
                        String txtFilePath = basePath + File.separator + appName + JavaCGConstants.EXT_TXT;
                        if (!graphGenService.readFileWriteToDb(txtFilePath, VERSION_FLAG)) {
                            log.error("version读取 txt文件内容，写入数据库，出现异常");
                            return false;
                        }

                        // 删除文件夹，以及文件夹以下的内容
                        if (!FileUtil.del(basePath)) {
                            log.error("version初始化删除文件，出现异常");
                            return false;
                        }
                        log.info("结束异步生成version内容：" + appName);
                        long spendTime = System.currentTimeMillis() - startTime;
                        log.info("{} 生成代码耗时: {} s", this.getClass().getSimpleName(), spendTime / 1000.0D);

                        // 去比较代码差异
                        log.info("开始比较代码差异：" + appName);
                        long startTime1 = System.currentTimeMillis();
                        boolean result = compareVersionAndTrunk(appName, auditVersion);
                        long spendTime1 = System.currentTimeMillis() - startTime1;
                        log.info("{} 代码比较耗时: {} s", this.getClass().getSimpleName(), spendTime1 / 1000.0D);
                        return result;
                    }, threadPoolExecutor)
                    .whenComplete((result, error) -> {
                        // 只要结束了，都需要修改状态
                        graphLogService.updateAuditLogToOldVersion(upId);
                        // 表示此appName已生成完成
                        singletonService.removeIsGeneratingVersionGraphRelation(appName);
                        if (result) {
                            // 记录成功日志
                            log.info("记录成功异步生成version内容日志：" + appName);
                            graphLogService.saveAuditLog(new AppOperateLogDTO.Builder().setAppName(appName).setAppVersion(auditVersion).setStatus(1).setVersion(1).build());
                        } else {
                            // 记录异常日志
                            log.error("记录失败异步生成version内容日志：" + appName);
                            graphLogService.saveAuditLog(new AppOperateLogDTO.Builder().setAppName(appName).setAppVersion(auditVersion).setStatus(2).setVersion(1).build());
                        }
                    });
        } catch (Exception e) {
            log.error("生成version调用链路出现异常：{}", e.getMessage());
        }
    }

    /**
     * 比较version和trunk的代码是否有区别
     */
    private boolean compareVersionAndTrunk(String appName, String version) {
        log.info(appName + "：开始比较代码");
        // 先查询出trunk的appNum
        Integer trunkAppNum = graphRuleMapper.selectTrunkAppNumByAppNameAndVersion(appName);

        // 统一查询相关的classHah、methodHash
        // 再查询出version的 appNum
        Integer versionAppNum = graphRuleMapper.selectVersionAppNumByAppNameAndVersion(appName, version);
        // 查询出trunk的所有class
        List<String> trunkClassList = graphRuleMapper.selectTrunkClassHashByAppNum(trunkAppNum);
        // 查询version的所有class
        List<String> versionClassList = graphRuleMapper.selectVersionClassHashByAppNum(versionAppNum);
        // 查询出version的所有method
        List<String> trunkMethodList = graphRuleMapper.selectTrunkMethodByAppNum(trunkAppNum);
        // 查询出trunk的所有method
        List<String> versionMethodList = graphRuleMapper.selectVersionMethodByAppNum(versionAppNum);
        log.info(appName + "：查询必备条件结束");

        // 先判断新增/删除
        // 删除的类list
        for (String trunkClassHash : trunkClassList) {
            // 移除之后，就是新增的类
            versionClassList.remove(trunkClassHash);
        }
        // 修改 新增的类
        if (CollUtil.isNotEmpty(versionClassList)) {
            // 更新version数据库
            graphRuleMapper.updateVersionClassStatus(versionClassList, 1);
        }

        // 可能修改的方法list
        List<String> mayUpMethodList = new ArrayList<>();
        for (String trunkMethodHash : trunkMethodList) {
            if (versionMethodList.contains(trunkMethodHash)) {
                mayUpMethodList.add(trunkMethodHash);
                // 移除之后，就是新增的方法
                versionMethodList.remove(trunkMethodHash);
            }
        }
        // 修改 新增的方法
        if (CollUtil.isNotEmpty(versionMethodList)) {
            // 更新version数据库
            graphRuleMapper.updateVersionMethodStatus(versionMethodList, 1);
        }

        log.info(appName + "：新增内容结束");
        // 真正修改的方法
        List<String> upVersionMethodList = new ArrayList<>();
        // 每个方法，只要查询它的那一层，如果没变就先暂定为未修改，如果此方法是变过的，则将所有调用此方法的方法，改为已修改，同时删除list中需要分析的方法（已经修改了，没必要再往下分析了）
        for (String methodHash : mayUpMethodList) {
            // 表示这个方法，已经修改过了
            if (upVersionMethodList.contains(methodHash)) {
                continue;
            }

            // 判断两个list是否相同
            // 先查询 trunk 的下一层调用List
            List<String> trunkMethodCallList = graphRuleMapper.selectTrunkDownMethodCallByAppNumAndMethodHash(trunkAppNum, methodHash);

            // 再查询 version 的下一层调用List
            List<String> versionMethodCallList = graphRuleMapper.selectVersionDownMethodCallByAppNumAndMethodHash(versionAppNum, methodHash);

            // 如果有不一致的地方，再递归查询到所有的方法
            if (CollUtil.isEqualList(trunkMethodCallList, versionMethodCallList)) {
                continue;
            }

            // 到这里就是顺序不一致了
            upVersionMethodList.add(methodHash);
            // 递归向上查询，所有调用此方法的方法，都表示是修改过的
            getUpFatherHash(upVersionMethodList, methodHash, versionAppNum);
        }

        log.info(appName + "：查询有区别的内容结束");
        // 通过有修改的方法，统一修改对应的类
        if (CollUtil.isNotEmpty(upVersionMethodList)) {
            List<String> upVersionClassList = graphRuleMapper.selectVersionClassHashByMethodHashList(upVersionMethodList);

            // 修改 修改过的方法的状态
            graphRuleMapper.updateVersionMethodStatus(upVersionMethodList, 2);
            // 修改 修改过类的状态
            graphRuleMapper.updateVersionClassStatus(upVersionClassList, 2);
        }

        log.info(appName + "：更新内容结束");
        return true;
    }

    /**
     * 递归，获取当前methodHash所有的调用方
     */
    private void getUpFatherHash(List<String> upMethodList, String methodHash, Integer versionAppNum) {
        // 查出对应的调用方
        List<String> upMethodHashList = graphRuleMapper.selectVersionUpMethodCallByAppNumAndMethodHash(versionAppNum, methodHash);

        // 接口及其实现
        MethodDetailDTO methodDetailDTO = graphRuleMapper.selectVersionMethodDetailByMethodHashAndAppNum(methodHash, versionAppNum);
        String calleeClassName = graphRelationMapper.selectItfCalleeClassNameByCallerClassHash(methodDetailDTO.getClassHash());
        // 表示有对应的接口
        if (StrUtil.isNotBlank(calleeClassName)) {
            upMethodHashList.addAll(graphRuleMapper.selectVersionUpMethodCallByAppNumAndMethodHash(
                    versionAppNum,
                    GraphJACGUtil.genHashWithLen(calleeClassName + FLAG_COLON + methodDetailDTO.getMethodFullName())));
            // 接口这个方法，也算是变了
            upMethodList.add(GraphJACGUtil.genHashWithLen(calleeClassName + FLAG_COLON + methodDetailDTO.getMethodFullName()));
        }

        if (CollUtil.isNotEmpty(upMethodHashList)) {
            for (String upMethodHash : upMethodHashList) {
                // 不用再往上查询了，往上的肯定都有
                if (upMethodList.contains(upMethodHash)) {
                    continue;
                }

                upMethodList.add(upMethodHash);
                getUpFatherHash(upMethodList, upMethodHash, versionAppNum);
            }
        }
    }

    @Override
    public CommonResultVO getBranchList(String appName) {
        // 存放参数
        Map<String, Object> param = new HashMap<>();
        param.put("app_name", appName);
        String result = HttpUtil.createGet(versionPath).form(param).execute().body();
        if (StrUtil.isNotBlank(result)) {
            return JSONUtil.toBean(result, CommonResultVO.class);
        }

        return null;
    }

    @Override
    public CommonResultVO getBranchPackList(String branchName) {
        // 存放参数
        Map<String, Object> param = new HashMap<>();
        param.put("branch_name", branchName);
        String result = HttpUtil.createGet(versionJarPath).form(param).execute().body();
        if (StrUtil.isNotBlank(result)) {
            CommonResultVO commonResultVO = JSONUtil.toBean(result, CommonResultVO.class);
            Object data = commonResultVO.getData();
            if (ObjectUtil.isEmpty(data)) {
                return commonResultVO;
            }
            List<String> pathList = (List<String>) data;
            List<AppPathDTO> appPathDTOList = new ArrayList<>();

            for (String path : pathList) {
                AppPathDTO appPathDTO = new AppPathDTO();

                appPathDTO.setPath(path);
                String[] split = path.split(COMMON_SEPARATOR);
                appPathDTO.setName(split[split.length - 2]);
                appPathDTOList.add(appPathDTO);
            }

            commonResultVO.setData(appPathDTOList);
            return commonResultVO;
        }

        return null;
    }

    /**
     * 确定要删除的旧版本内容
     */
    private List<Integer> getDeleteNumList(List<AppInfoDTO> appInfoVOList, String appVersion) {
        List<Integer> deleteNumList = new ArrayList<>();

        if (appInfoVOList.size() >= 3) {
            // 因为id默认是从小到大的，所以for循环从后往前，跳过新的版本
            for (int i = appInfoVOList.size() - 1; i >= 0; i--) {
                // 如果是之前存在了一模一样的版本，则只删除之前的版本，重新分析此版本
                if (appVersion.equals(appInfoVOList.get(i).getAppVersion())) {
                    deleteNumList.clear();
                    deleteNumList.add(appInfoVOList.get(i).getId());
                    break;
                }
                if (appInfoVOList.size() - i <= 2) {
                    continue;
                }
                // 判断是否超过了3个版本，只有超过了3个版本，才删除最老的版本。
                deleteNumList.add(appInfoVOList.get(i).getId());
            }
        }
        // <= 3 时，也需要查看是否重复生成相同版本的内容
        else {
            for (int i = appInfoVOList.size() - 1; i >= 0; i--) {
                // 如果是之前存在了一模一样的版本，则只删除之前的版本，重新分析此版本
                if (appVersion.equals(appInfoVOList.get(i).getAppVersion())) {
                    deleteNumList.add(appInfoVOList.get(i).getId());
                    break;
                }
            }
        }
        return deleteNumList;
    }

    /**
     * 删除5张表的内容：graph_app_info、graph_class_call、graph_class_detail、graph_method_call、graph_method_detail
     */
    private boolean deleteTrunkInfo(List<Integer> appNumList) {
        try {
            return graphRelationMapper.deleteAppInfoByAppNum(appNumList) >= 0
                    && graphRelationMapper.deleteClassCallByAppNum(appNumList) >= 0
                    && graphRelationMapper.deleteClassDetailByAppNum(appNumList) >= 0
                    && graphRelationMapper.deleteMethodCallByAppNum(appNumList) >= 0
                    && graphRelationMapper.deleteMethodDetailByAppNum(appNumList) >= 0;
        } catch (Exception e) {
            log.error("删除表内容出现异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 删除对应version的内容
     *
     * @param appNumList app的唯一标识
     */
    private boolean deleteVersionInfo(List<Integer> appNumList) {
        try {
            return graphRelationMapper.deleteVersionAppInfoByAppNum(appNumList) >= 0
                    && graphRelationMapper.deleteVersionClassCallByAppNum(appNumList) >= 0
                    && graphRelationMapper.deleteVersionClassDetailByAppNum(appNumList) >= 0
                    && graphRelationMapper.deleteVersionMethodCallByAppNum(appNumList) >= 0
                    && graphRelationMapper.deleteVersionMethodDetailByAppNum(appNumList) >= 0;
        } catch (Exception e) {
            log.error("删除表内容出现异常：{}", e.getMessage());
            return false;
        }
    }

    @Override
    @Trace
    @Tags({
            @Tag(key = "appName", value = "arg[0].appName"),
            @Tag(key = "appVersion", value = "arg[0].appVersion"),
            @Tag(key = "id", value = "returnedObj.id")
    })
    public MethodRelationVO getDownRelation(MethodDownRelationInputVO inputVO) {
        log.info("向下调用链，获取入参appName:" + inputVO.getAppName());

        // 获取过滤条件
        List<String> filterList = new ArrayList<>();
        if (inputVO.getShowAll() == 1) {
            filterList = graphRuleMapper.selectRuleByAppName(inputVO.getAppName());
        }

        MethodRelationVO methodRelationVO = new MethodRelationVO();
        methodRelationVO.setEnd(true);
        String methodHash = inputVO.getMethodHash();

        // 表示查询 TRUNK
        if (inputVO.getAppType() == 0) {
            MethodRelationDTO methodDetail = graphRelationMapper.selectMethodDetailByMethodHash(methodHash);

            // 接口实现类.接口方法()、接口.接口方法()必定是在 方法详情表graph_method_detail 中的，不会进入此else
            // 所以这里也有可能是，调用 子类.父类方法() ，这里传入的 methodHash 必定是 graph_method_call 中的被调用方，因为调用方必定在类详情表 graph_method_detail中
            if (methodDetail == null) {
                methodDetail = graphRelationMapper.selectCalleeMethodInfoByMethodHash(methodHash);
                if (methodDetail == null) {
                    return null;
                }

                methodDetail.setAppNum(graphRelationMapper.selectClassAppNumByClassHash(methodDetail.getClassHash()));
            }

            // 设置根节点内容
            // Trunk都为0
            methodDetail.setStatus(0);
            setDownRootMethodInfo(methodRelationVO, methodDetail, methodHash);
            methodRelationVO.setChildren(getChildrenDownRelationTrunk(methodRelationVO, null, methodHash, methodDetail.getClassHash(), methodDetail.getMethodFullName(),
                    inputVO.getDepth(), 0, filterList, methodDetail.getAppNum(), inputVO.getInApp()));
        }
        // 查询 Version
        else {
            Integer appNum = graphRelationMapper.selectVersionAppNum(inputVO.getAppName(), inputVO.getAppVersion());
            // 入参错误
            if (appNum == null) {
                return null;
            }

            MethodRelationDTO methodDetail = graphRelationMapper.selectVersionMethodDetailByMethodHash(methodHash, appNum);
            if (methodDetail == null) {
                methodDetail = graphRelationMapper.selectVersionCalleeMethodInfoByMethodHash(methodHash, appNum);
                if (methodDetail == null) {
                    return null;
                }
                methodDetail.setAppNum(graphRelationMapper.selectVersionClassAppNumByClassHash(methodDetail.getClassHash(), appNum));
                // 查不到的都设置为0
                methodDetail.setStatus(0);
            }

            // 设置根节点内容
            setDownRootMethodInfo(methodRelationVO, methodDetail, methodHash);
            // 只有version需要单独设置status
            methodRelationVO.setChildren(getChildrenDownRelationVersion(methodRelationVO, null, methodHash, methodDetail.getClassHash(), methodDetail.getMethodFullName(),
                    inputVO.getDepth(), 0, filterList, methodDetail.getAppNum()));
        }

        return methodRelationVO;
    }

    /**
     * 获取version的向下调用链
     */
    private List<MethodRelationVO> getChildrenDownRelationVersion(MethodRelationVO root,
                                                                  MethodRelationVO childrenVO,
                                                                  String methodHash,
                                                                  String classHash,
                                                                  String methodFullName,
                                                                  Integer depth,
                                                                  Integer startNum,
                                                                  List<String> filterList,
                                                                  Integer appNum) {
        // 获取当前方法的 被调用方法的集合
        ArrayList<MethodRelationVO> childrenList = new ArrayList<>();
        List<MethodRelationDTO> calleeList = graphRelationMapper.selectVersionDownRelationByMethodHash(methodHash, appNum);

        // 当前子节点为空，需要去查看，是否是子类调用、或者impl调用
        // 先检查 传入的是接口的情况
        if (CollUtil.isEmpty(calleeList)) {
            // 通过classHash查询出在类调用表中，是否有实现类
            List<String> classFullNameList = graphRelationMapper.selectVersionClassNameInInterface(classHash, appNum);

            for (String classFullName : classFullNameList) {
                // 正常来说，graph_method_detail 都会有这些实现方法的记录
                String iMethodHash = GraphJACGUtil.genHashWithLen(classFullName + FLAG_COLON + methodFullName);
                MethodRelationDTO iMethodRelationDTO = graphRelationMapper.selectVersionMethodDetailByMethodHash(iMethodHash, appNum);
                if (iMethodRelationDTO != null) {
                    iMethodRelationDTO.setCallType(CallTypeEnum.CTE_ITF.getType());
                    iMethodRelationDTO.setCalleeMethodHash(iMethodHash);
                    calleeList.add(iMethodRelationDTO);
                }
            }
        }

        // 再检查父子类调用的情况，又要递归--|
        if (CollUtil.isEmpty(calleeList)) {
            // 再检查父子类调用的情况，就子类而言，java是没有多个父类的，所以返回的值只可能唯一
            getExtendsDownRelationVersion(classHash, calleeList, methodFullName, appNum);
        }

        // 不超过递归的层数
        if (++startNum > depth) {
            // 表示还未完结
            if (CollUtil.isNotEmpty(calleeList)) {
                root.setEnd(false);
                childrenVO.setEnd(false);
            }
            return new ArrayList<>();
        }

        // 既没有接口调用，也没有父子类调用，表示确实是最后一层
        if (CollUtil.isEmpty(calleeList)) {
            return new ArrayList<>();
        }

        // 作为被调用方，添加属性
        for (MethodRelationDTO methodRelationDTO : calleeList) {
            if (checkFilterRule(filterList, methodRelationDTO.getClassFullName())) {
                continue;
            }

            // 把被调用方,当成调用方
            String calleeMethodHash = methodRelationDTO.getCalleeMethodHash();
            Integer curAppNum;
            classHash = methodRelationDTO.getClassHash();
            // 被调用方法的详情，是有可能为null的
            MethodRelationDTO methodDetail = graphRelationMapper.selectVersionMethodDetailByMethodHash(calleeMethodHash, appNum);

            // 拼接参数：上一个节点对此节点的调用关系
            childrenVO = new MethodRelationVO();
            childrenVO.setEnd(true);
            if (methodDetail != null) {
                curAppNum = methodDetail.getAppNum();
                childrenVO.setAnnotation(methodDetail.getAnnotationText());
                childrenVO.setStatus(methodDetail.getStatus());
            } else {
                // 查看当前类，是在哪个App
                curAppNum = graphRelationMapper.selectVersionClassAppNumByClassHash(classHash, appNum);
                // 其余的被调用方法，就是当成未修改过的
                childrenVO.setStatus(0);
            }
            childrenVO.setAppNum(curAppNum);
            setDownChildrenMethodInfo(childrenVO, methodRelationDTO, calleeMethodHash);

            // 这里查的只有同个App内的，在不是一个App内的，则跳过
            if (curAppNum == null || !NumberUtil.equals(appNum, curAppNum)) {
                childrenVO.setChildren(new ArrayList<>());
                childrenList.add(childrenVO);
                continue;
            }

            childrenVO.setChildren(getChildrenDownRelationVersion(root, childrenVO, calleeMethodHash, classHash, methodRelationDTO.getMethodFullName(), depth, startNum, filterList, appNum));
            childrenList.add(childrenVO);
        }

        return childrenList;
    }

    /**
     * version的父子类关系查询
     */
    private void getExtendsDownRelationVersion(String classHash, List<MethodRelationDTO> calleeList, String methodFullName, Integer appNum) {
        // 表示这个方法的父类 到头了
        String classFullName = graphRelationMapper.selectVersionClassNameInExtendsFather(classHash, appNum);
        if (StrUtil.isEmpty(classFullName)) {
            return;
        }

        String fMethodHash = GraphJACGUtil.genHashWithLen(classFullName + FLAG_COLON + methodFullName);
        MethodRelationDTO iMethodRelationDTO = graphRelationMapper.selectVersionMethodDetailByMethodHash(fMethodHash, appNum);
        if (iMethodRelationDTO != null) {
            iMethodRelationDTO.setCallType(CallTypeEnum.CTE_CCS.getType());
            iMethodRelationDTO.setCalleeMethodHash(fMethodHash);
            calleeList.add(iMethodRelationDTO);
            return;
        }
        getExtendsDownRelationVersion(GraphJACGUtil.genHashWithLen(classFullName), calleeList, methodFullName, appNum);
    }

    /**
     * 封装向下调用链的根节点内容
     */
    private void setDownRootMethodInfo(MethodRelationVO methodRelationVO, MethodRelationDTO methodDetail, String methodHash) {
        methodRelationVO.setMethodName(methodDetail.getMethodName());
        methodRelationVO.setLabel(methodDetail.getMethodName());
        methodRelationVO.setMethodHash(methodHash);
        methodRelationVO.setId(methodHash);
        methodRelationVO.setAnnotation(methodDetail.getAnnotationText());
        methodRelationVO.setAppNum(methodDetail.getAppNum());
        methodRelationVO.setClassFullName(methodDetail.getClassFullName());
        methodRelationVO.setMethodFullName(methodDetail.getMethodFullName());
        methodRelationVO.setStatus(methodDetail.getStatus());
    }

    /**
     * 递归生成完整的树，向下调用
     */
    private List<MethodRelationVO> getChildrenDownRelationTrunk(MethodRelationVO root,
                                                                MethodRelationVO childrenVO,
                                                                String methodHash,
                                                                String classHash,
                                                                String methodFullName,
                                                                Integer depth,
                                                                Integer startNum,
                                                                List<String> filterList,
                                                                Integer appNum,
                                                                Integer inApp) {
        // 获取当前方法的 被调用方法的集合
        ArrayList<MethodRelationVO> childrenList = new ArrayList<>();
        List<MethodRelationDTO> calleeList = graphRelationMapper.selectDownRelationByMethodHash(methodHash);

        // 当前子节点为空，需要去查看，是否是子类调用、或者impl调用
        // 先检查 传入的是接口的情况
        if (CollUtil.isEmpty(calleeList)) {
            // 通过classHash查询出在类调用表中，是否有实现类
            List<String> classFullNameList = graphRelationMapper.selectClassNameInInterface(classHash);

            for (String classFullName : classFullNameList) {
                // 正常来说，graph_method_detail 都会有这些实现方法的记录
                String iMethodHash = GraphJACGUtil.genHashWithLen(classFullName + FLAG_COLON + methodFullName);
                MethodRelationDTO iMethodRelationDTO = graphRelationMapper.selectMethodDetailByMethodHash(iMethodHash);
                if (iMethodRelationDTO != null) {
                    iMethodRelationDTO.setCallType(CallTypeEnum.CTE_ITF.getType());
                    iMethodRelationDTO.setCalleeMethodHash(iMethodHash);
                    calleeList.add(iMethodRelationDTO);
                }
            }
        }

        // 再检查父子类调用的情况，又要递归--|
        if (CollUtil.isEmpty(calleeList)) {
            getExtendsDownRelation(classHash, calleeList, methodFullName, appNum, inApp);
        }

        // 不超过递归的层数
        if (++startNum > depth) {
            // 表示还未完结
            if (CollUtil.isNotEmpty(calleeList)) {
                root.setEnd(false);
                childrenVO.setEnd(false);
            }
            return new ArrayList<>();
        }

        // 既没有接口调用，也没有父子类调用，表示确实是最后一层
        if (CollUtil.isEmpty(calleeList)) {
            return new ArrayList<>();
        }

        // 作为被调用方，添加属性
        for (MethodRelationDTO methodRelationDTO : calleeList) {
            if (checkFilterRule(filterList, methodRelationDTO.getClassFullName())) {
                continue;
            }

            // 把被调用方,当成调用方
            String calleeMethodHash = methodRelationDTO.getCalleeMethodHash();
            Integer curAppNum;
            classHash = methodRelationDTO.getClassHash();
            // 被调用方法的详情，是有可能为null的
            MethodRelationDTO methodDetail = graphRelationMapper.selectMethodDetailByMethodHash(calleeMethodHash);

            // 拼接参数：上一个节点对此节点的调用关系
            childrenVO = new MethodRelationVO();
            childrenVO.setEnd(true);
            // Trunk全部为0
            childrenVO.setStatus(0);
            if (methodDetail != null) {
                curAppNum = methodDetail.getAppNum();
                childrenVO.setAnnotation(methodDetail.getAnnotationText());
            } else {
                // 查看当前类，是在哪个App
                curAppNum = graphRelationMapper.selectClassAppNumByClassHash(classHash);
            }
            childrenVO.setAppNum(curAppNum);
            setDownChildrenMethodInfo(childrenVO, methodRelationDTO, calleeMethodHash);

            // 表示在App内，则跳过
            if (inApp == 1) {
                if (curAppNum == null || !NumberUtil.equals(appNum, curAppNum)) {
                    childrenVO.setChildren(new ArrayList<>());
                    childrenList.add(childrenVO);
                    continue;
                }
            }
            childrenVO.setChildren(getChildrenDownRelationTrunk(root, childrenVO, calleeMethodHash, classHash, methodRelationDTO.getMethodFullName(), depth, startNum, filterList, appNum, inApp));
            childrenList.add(childrenVO);
        }

        return childrenList;
    }

    /**
     * 设置向下调用 子节点的方法详情
     */
    private void setDownChildrenMethodInfo(MethodRelationVO childrenVO, MethodRelationDTO methodRelationDTO, String calleeMethodHash) {
        childrenVO.setCallType(methodRelationDTO.getCallType());
        childrenVO.setMethodLineNum(methodRelationDTO.getCallerMethodLineNum());
        childrenVO.setMethodHash(calleeMethodHash);
        childrenVO.setId(calleeMethodHash);
        childrenVO.setMethodName(methodRelationDTO.getMethodName());
        childrenVO.setMethodFullName(methodRelationDTO.getMethodFullName());
        childrenVO.setLabel(methodRelationDTO.getMethodName());
        childrenVO.setClassFullName(methodRelationDTO.getClassFullName());
    }

    /**
     * 父子类的递归调用，通过子类查父类
     */
    private void getExtendsDownRelation(String classHash, List<MethodRelationDTO> calleeList, String methodFullName, Integer appNum, Integer inApp) {
        // 表示这个方法的父类 到头了
        String classFullName = graphRelationMapper.selectClassNameInExtendsFather(classHash);
        if (StrUtil.isEmpty(classFullName)) {
            return;
        }

        String fMethodHash = GraphJACGUtil.genHashWithLen(classFullName + FLAG_COLON + methodFullName);
        MethodRelationDTO iMethodRelationDTO = graphRelationMapper.selectMethodDetailByMethodHash(fMethodHash);
        if (iMethodRelationDTO != null) {
            // 只能在当前的模块中去查找调用关系，当父类不在此模块时，则不再往下递归
            if (inApp == 1 && !NumberUtil.equals(iMethodRelationDTO.getAppNum(), appNum)) {
                return;
            }
            iMethodRelationDTO.setCallType(CallTypeEnum.CTE_CCS.getType());
            iMethodRelationDTO.setCalleeMethodHash(fMethodHash);
            calleeList.add(iMethodRelationDTO);
            return;
        }
        getExtendsDownRelation(GraphJACGUtil.genHashWithLen(classFullName), calleeList, methodFullName, appNum, inApp);
    }

    @Override
    public MethodUpRelationVO getUpRelation(MethodUpRelationInputVO inputVO) {
        // 获取过滤条件
        List<String> filterList = new ArrayList<>();
        if (inputVO.getShowAll() == 1) {
            filterList = graphRuleMapper.selectRuleByAppName(inputVO.getAppName());
        }

        MethodUpRelationVO methodUpRelationVO = new MethodUpRelationVO();
        String methodHash = inputVO.getMethodHash();
        MethodRelationDTO methodDetail = graphRelationMapper.selectMethodDetailByMethodHash(methodHash);

        // 接口实现类.接口方法()、接口.接口方法()必定是在 方法详情表graph_method_detail 中的，不会进入此else
        // 所以这里也有可能是，调用 子类.父类方法() ，这里传入的 methodHash 必定是 graph_method_call 中的被调用方，因为调用方必定在类详情表 graph_method_detail中
        if (methodDetail == null) {
            methodDetail = graphRelationMapper.selectCalleeMethodInfoByMethodHash(methodHash);
            if (methodDetail == null) {
                return null;
            }
            methodDetail.setAppNum(graphRelationMapper.selectClassAppNumByClassHash(methodDetail.getClassHash()));
        }

        setUpRootMethodInfo(methodUpRelationVO, methodDetail, methodHash);
        methodUpRelationVO.setChildren(getChildrenUpRelation(methodHash, methodDetail.getClassHash(), methodDetail.getMethodFullName(), filterList));
        return methodUpRelationVO;
    }

    /**
     * 封装向上调用链的根节点内容
     */
    private void setUpRootMethodInfo(MethodUpRelationVO methodUpRelationVO, MethodRelationDTO methodDetail, String methodHash) {
        methodUpRelationVO.setMethodName(methodDetail.getMethodName());
        methodUpRelationVO.setLabel(methodDetail.getMethodName());
        methodUpRelationVO.setMethodHash(methodHash);
        methodUpRelationVO.setId(methodHash);
        methodUpRelationVO.setAnnotation(methodDetail.getAnnotationText());
        methodUpRelationVO.setAppNum(methodDetail.getAppNum());
        methodUpRelationVO.setClassFullName(methodDetail.getClassFullName());
        methodUpRelationVO.setMethodFullName(methodDetail.getMethodFullName());
    }

    /**
     * 向上调用，没有使用递归，就一层
     */
    private List<MethodUpRelationVO> getChildrenUpRelation(String methodHash, String classHash, String methodFullName, List<String> filterList) {
        // 获取当前方法的 调用方法的集合
        List<MethodUpRelationVO> result = new ArrayList<>();
        List<MethodRelationDTO> callerList = graphRelationMapper.selectUpRelationByMethodHash(methodHash);

        // 如果传入的是接口实现类
        if (CollUtil.isEmpty(callerList)) {
            // 通过classHash查询出在类调用表中，是否有接口类，有可能实现了多个接口，多个接口还有同名的方法--|
            List<String> classFullNameList = graphRelationMapper.selectClassNameInInterfaceImpl(classHash);

            for (String classFullName : classFullNameList) {
                // 正常来说，graph_method_detail 都会有这些实现方法的记录
                String iMethodHash = GraphJACGUtil.genHashWithLen(classFullName + FLAG_COLON + methodFullName);
                MethodRelationDTO iMethodRelationDTO = graphRelationMapper.selectMethodDetailByMethodHash(iMethodHash);
                if (iMethodRelationDTO != null) {
                    iMethodRelationDTO.setCallType(CallTypeEnum.CTE_ITF.getType());
                    iMethodRelationDTO.setCallerMethodHash(iMethodHash);
                    callerList.add(iMethodRelationDTO);
                }
            }
        }

        // 如果传入的是 父类.父类方法()，需要去查看所有的子类有没有对应的调用，如果子类有重写则不计入其中
        if (CollUtil.isEmpty(callerList)) {
            getExtendsUpRelation(classHash, callerList, methodFullName);
        }

        if (CollUtil.isEmpty(callerList)) {
            return new ArrayList<>();
        }

        // 作为被调用方，添加属性
        for (MethodRelationDTO methodRelationDTO : callerList) {
            String callerMethodHash = methodRelationDTO.getCallerMethodHash();
            // 只要不是父子类调用的情况，methodDetail 都不会为null
            MethodRelationDTO methodDetail = graphRelationMapper.selectMethodDetailByMethodHash(callerMethodHash);
            if (ObjectUtil.isNotNull(methodDetail)) {
                methodRelationDTO.setClassFullName(methodDetail.getClassFullName());
            }
            // 使用校验规则
            if (checkFilterRule(filterList, methodRelationDTO.getClassFullName())) {
                continue;
            }

            // 开始拼接参数
            MethodUpRelationVO childrenUpVO = new MethodUpRelationVO();
            if (ObjectUtil.isNotNull(methodDetail)) {

                setUpChildrenMethodInfo(childrenUpVO, methodDetail);
            }
            // 父子类调用的情况，是把详情放在了 methodRelationDTO
            else {
                setUpChildrenMethodInfoExtends(childrenUpVO, methodRelationDTO);
            }

            childrenUpVO.setMethodHash(callerMethodHash);
            childrenUpVO.setId(callerMethodHash);
            childrenUpVO.setCallType(methodRelationDTO.getCallType());
            childrenUpVO.setChildren(new ArrayList<>());

            // 赋值给返回结果
            result.add(childrenUpVO);
        }

        return result;
    }

    /**
     * 父子类调用的拼接结果
     */
    private void setUpChildrenMethodInfoExtends(MethodUpRelationVO childrenUpVO, MethodRelationDTO methodRelationDTO) {
        childrenUpVO.setMethodName(methodRelationDTO.getMethodName());
        childrenUpVO.setLabel(methodRelationDTO.getMethodName());
        childrenUpVO.setAppNum(methodRelationDTO.getAppNum());
        childrenUpVO.setClassFullName(methodRelationDTO.getClassFullName());
        childrenUpVO.setMethodFullName(methodRelationDTO.getMethodFullName());
    }

    /**
     * 接口、普通调用方的拼接结果
     */
    private void setUpChildrenMethodInfo(MethodUpRelationVO childrenUpVO, MethodRelationDTO methodDetail) {
        childrenUpVO.setMethodName(methodDetail.getMethodName());
        childrenUpVO.setLabel(methodDetail.getMethodName());
        childrenUpVO.setMethodLineNum(methodDetail.getCallerMethodLineNum());
        childrenUpVO.setAppNum(methodDetail.getAppNum());
        childrenUpVO.setAnnotation(methodDetail.getAnnotationText());
        childrenUpVO.setClassFullName(methodDetail.getClassFullName());
        childrenUpVO.setMethodFullName(methodDetail.getMethodFullName());
    }

    /**
     * 父子类的递归调用，通过父类查子类
     */
    private void getExtendsUpRelation(String classHash, List<MethodRelationDTO> callerList, String methodFullName) {
        List<String> classFullNameList = graphRelationMapper.selectClassNameInExtendsSon(classHash);

        for (String classFullName : classFullNameList) {
            String fMethodHash = GraphJACGUtil.genHashWithLen(classFullName + FLAG_COLON + methodFullName);
            MethodRelationDTO iMethodRelationDTO = graphRelationMapper.selectMethodDetailByMethodHash(fMethodHash);
            // 表示子类自己也有这个方法，那么就不会调到父类中了
            if (iMethodRelationDTO != null) {
                continue;
            }

            // 看一下当前 子类.方法() 是否有被调用过，只有被调用过的，才会记录
            MethodRelationDTO methodRelationDTO = graphRelationMapper.selectCalleeMethodInfoByMethodHash(fMethodHash);
            if (methodRelationDTO != null) {
                // 还要一个appNum
                methodRelationDTO.setAppNum(graphRelationMapper.selectClassAppNumByClassHash(methodRelationDTO.getClassHash()));
                methodRelationDTO.setCallerMethodHash(fMethodHash);
                methodRelationDTO.setCallType(CallTypeEnum.CTE_CCS.getType());
                callerList.add(methodRelationDTO);
                continue;
            }

            // 如果没有被调用过，可能当前子类的子类，有可能被调用过，递归往下查询
            getExtendsUpRelation(GraphJACGUtil.genHashWithLen(classFullName), callerList, methodFullName);
        }
    }

    /**
     * 校验过滤规则
     */
    private boolean checkFilterRule(List<String> filterList, String classFullName) {
//        log.info("校验过滤规则 classFullName:" + classFullName);
        if (CollUtil.isEmpty(filterList)) {
            return false;
        }

        for (String endFilter : filterList) {
            // 满足过滤条件
            if (classFullName.toUpperCase().endsWith(endFilter)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建空表
     *
     * @return 建表成功/失败
     */
    private boolean createTables() {
        return graphRelationMapper.createGraphJarInfoTable(TABLE_JAR_INFO) == 0
                && graphRelationMapper.createGraphClassCallTable(TABLE_CLASS_CALL) == 0
                && graphRelationMapper.createGraphClassDetailTable(TABLE_CLASS_DETAIL) == 0
                && graphRelationMapper.createGraphMethodCallTable(TABLE_METHOD_CALL) == 0
                && graphRelationMapper.createGraphMethodDetailTable(TABLE_METHOD_DETAIL) == 0;
    }

    /**
     * 批量删除
     *
     * @return 删表成功/失败
     */
    private boolean dropFormerTable() {
        // drop table if exists
        try {
            return graphRelationMapper.patchDropFormerTable(getTableList()) == 0;
        } catch (Exception e) {
            log.error("删除表出现异常：{}", e.getMessage());
            return false;
        }
    }
}
