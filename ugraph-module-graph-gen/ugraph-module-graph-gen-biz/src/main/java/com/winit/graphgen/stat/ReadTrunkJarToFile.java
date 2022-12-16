package com.winit.graphgen.stat;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.winit.graph.common.entity.CallTypeEnum;
import com.winit.graph.common.entity.JavaCGConstants;
import com.winit.graphgen.entity.dto.AppInfoDto;
import com.winit.graphgen.entity.graph.CallIdCounter;
import com.winit.graphgen.entity.graph.MethodCallInfoDto;
import com.winit.graphgen.mapper.GraphGenMapper;
import org.apache.bcel.classfile.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.winit.graph.common.entity.JavaCGConstants.*;

/**
 * 读取jar包内容 存入txt文件
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ReadTrunkJarToFile {

    private static final Logger log = LoggerFactory.getLogger(ReadTrunkJarToFile.class);

    @Autowired
    private GraphGenMapper graphGenMapper;

    private Map<String, Set<String>> calleeMethodMapGlobal;
    private Map<String, Boolean> runnableImplClassMap;
    private Map<String, Boolean> callableImplClassMap;
    private Map<String, Boolean> threadChildClassMap;
    private final CallIdCounter callIdCounter = CallIdCounter.newInstance();

    /**
     * jar 对应的序号（自增id）
     */
    private Integer maxAppNum = -1;

    /**
     * 重点，读取jar的信息，写入file文件
     *
     * @param jarPath  jar包路径
     * @param filePath 输出txt文件的根目录
     * @param fileName 输出txt文件的名称
     * @param flag     0表示TRUNK 1表示version
     */
    public boolean writeJarInfo(String jarPath,
                                String filePath,
                                String fileName,
                                Integer flag,
                                String version) {
        // calleeMethodMapGlobal 在处理所有的jar包时都需要使用，只初始化一次
        calleeMethodMapGlobal = new HashMap<>(INIT_SIZE_1000);

        // 存储文件路径
        String txtPath = filePath + File.separator + fileName + JavaCGConstants.EXT_TXT;
        log.info("写入方法调用文件:\n" + txtPath);

        // 开始写入信息
        try (Writer resultWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txtPath), StandardCharsets.UTF_8))
        ) {
            File jarFile = new File(jarPath);
            // 重点：处理一个jar包
            return handleOneJar(fileName, filePath, jarFile, jarPath, resultWriter, flag, version);
        } catch (IOException e) {
            log.error("处理jar包出现异常: {}", e.getMessage());
            return false;
        }
    }

    private void init() {
        runnableImplClassMap = new HashMap<>(INIT_SIZE_100);
        callableImplClassMap = new HashMap<>(INIT_SIZE_100);
        threadChildClassMap = new HashMap<>(INIT_SIZE_100);
    }

    /**
     * 重点：处理一个jar包
     *
     * @param jarFile      jar的File
     * @param jarPath      jar的实际路径
     * @param resultWriter 调用关系的 writer
     * @param flag         0表示TRUNK 1表示version
     * @return 处理是否成功
     */
    private boolean handleOneJar(String fileName,
                                 String filePath,
                                 File jarFile,
                                 String jarPath,
                                 Writer resultWriter,
                                 Integer flag,
                                 String version) {
        try (JarFile jar = new JarFile(jarFile)) {
            // 初始化
            init();

            // 获取配置文件中的
            // Ugraph-Analysis-Name
            String value = jar.getManifest().getMainAttributes().getValue(UGRAPH_ANALYSIS_NAME);
            List<String> otherJarList = new ArrayList<>();
            if (StrUtil.isNotEmpty(value)) {
                otherJarList = getOtherJarList(value.trim());
            }

            // 需要分析的jar包集合，这里存储的是完整的路径
            List<String> analysisList = new ArrayList<>();

            // 处理当前jar包中的class文件
            Enumeration<JarEntry> enumeration = jar.entries();
            // 用于计数第几次处理这个jar
            int count = 0;
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = enumeration.nextElement();
                if (!jarEntry.isDirectory() && jarEntry.getName().toLowerCase().endsWith(JavaCGConstants.EXT_CLASS)) {
                    // 首次添加，需要记录主jar的信息
                    if (count == 0) {
                        AppInfoDto appInfoDto = handleOneJarInfo(jarPath, flag, version, fileName);
                        maxAppNum = appInfoDto.getId();
                        String line = FILE_KEY_JAR_INFO_PREFIX + maxAppNum + " " + jarPath;
                        writeResult(resultWriter, line);
                        count++;
                    }
                    // 处理一个class文件
                    handleOneClass(jarPath, jarEntry, resultWriter);
                }
                // 符合规则的jar，存入list，继续分析
                if (!jarEntry.isDirectory() && (jarEntry.getName().toLowerCase().endsWith(JavaCGConstants.EXT_JAR) || jarEntry.getName().toLowerCase().endsWith(JavaCGConstants.EXT_WAR))) {
                    checkAddAnalysisList(fileName, filePath, jarEntry.getName(), otherJarList, analysisList);
                }
            }

            // 统一清理缓存
            graphClearCache();

            // 表示还需要分析当前 BOOT-INF/lib 下的某些jar
            if (CollUtil.isNotEmpty(analysisList)) {
                if (!handleAnalysisJar(resultWriter, analysisList)) {
                    return false;
                }
            }

            maxAppNum = -1;
            return true;
        } catch (Exception e) {
            log.error("处理jar包出现异常,{}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 分析其余的jar包
     */
    private boolean handleAnalysisJar(Writer resultWriter, List<String> analysisList) {
        for (String jarPath : analysisList) {
            try (JarFile jar = new JarFile(jarPath)) {
                // 初始化
                init();

                // 处理当前jar包中的class文件
                Enumeration<JarEntry> enumeration = jar.entries();
                while (enumeration.hasMoreElements()) {
                    JarEntry jarEntry = enumeration.nextElement();
                    if (!jarEntry.isDirectory() && jarEntry.getName().toLowerCase().endsWith(JavaCGConstants.EXT_CLASS)) {
                        // 处理一个class文件
                        handleOneClass(jarPath, jarEntry, resultWriter);
                    }
                }

                // 统一清理缓存
                graphClearCache();
            } catch (Exception e) {
                log.error("处理jar包出现异常,{}", e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 添加需要分析的jar完整路径
     */
    private void checkAddAnalysisList(String fileName,
                                      String filePath,
                                      String name,
                                      List<String> otherJarList,
                                      List<String> analysisList) {
        for (String otherJarName : otherJarList) {
            if (name.toLowerCase(Locale.ROOT).contains(otherJarName.toLowerCase())) {
                // "BOOT-INF" + File.separator + "lib" + File.separator +
                analysisList.add(filePath + File.separator + fileName + File.separator + name);
            }
        }
    }

    /**
     * 获取还需要分析的jar
     */
    private List<String> getOtherJarList(String value) {
        List<String> result = new ArrayList<>();
        for (String jarName : value.split(FLAG_SEMI)) {
            result.add(jarName.trim());
        }
        return result;
    }

    private void graphClearCache() {
        runnableImplClassMap.clear();
        callableImplClassMap.clear();
        threadChildClassMap.clear();
    }

    /**
     * 重点：处理一个class文件
     *
     * @param jarFilePath  jar的实际路径
     * @param jarEntry
     * @param resultWriter 调用关系的 writer
     */
    private void handleOneClass(String jarFilePath,
                                JarEntry jarEntry,
                                Writer resultWriter) {
        String jarEntryName = jarEntry.getName();
        try {
            ClassParser cp = new ClassParser(jarFilePath, jarEntryName);
            JavaClass javaClass = cp.parse();

            // 处理class
            ClassVisitor classVisitor = new ClassVisitor(javaClass);
            classVisitor.setCalleeMethodMapGlobal(calleeMethodMapGlobal);
            classVisitor.setRunnableImplClassMap(runnableImplClassMap);
            classVisitor.setCallableImplClassMap(callableImplClassMap);
            classVisitor.setThreadChildClassMap(threadChildClassMap);
            classVisitor.setCallIdCounter(callIdCounter);
            classVisitor.setRecordAll(false);
            // 这里写入注解信息，并添加方法调用信息
            classVisitor.start(maxAppNum);

            // 先记录一行类名
            writeResult(resultWriter, FILE_KEY_CLASS_NAME_PREFIX + javaClass.getClassName() + getAnnotation(javaClass.getAnnotationEntries(), GRAPH_CLASS_ANNOTATION_NAME) + " " + maxAppNum);

            // 记录父子类的调用关系
            String superclassName = javaClass.getSuperclassName();
            if (StrUtil.isNotEmpty(superclassName) && superclassName.startsWith(FILTER_PACKAGE_NAME)) {
                // 只记录子类调用父类
                writeResult(resultWriter, FILE_KEY_CLASS_PREFIX + javaClass.getClassName() + " " + FLAG_LEFT_BRACKET + CallTypeEnum.CTE_CCS.getType() + FLAG_RIGHT_BRACKET + superclassName + " " + maxAppNum);
            }
            // 记录接口、impl的调用关系
            String[] interfaceNames = javaClass.getInterfaceNames();
            if (ArrayUtil.isNotEmpty(interfaceNames)) {
                // 接口调用实现类
                for (String interfaceName : interfaceNames) {
                    if (interfaceName.startsWith(FILTER_PACKAGE_NAME)) {
                        writeResult(resultWriter, FILE_KEY_CLASS_PREFIX + javaClass.getClassName() + " " + FLAG_LEFT_BRACKET + CallTypeEnum.CTE_ITF.getType() + FLAG_RIGHT_BRACKET + interfaceName + " " + maxAppNum);
                    }
                }
            }

            // 记录类拥有的方法
            for (Method method : javaClass.getMethods()) {
                if (method.getName().contains(INIT_METHOD) || method.getName().contains(CLINIT_METHOD)) {
                    continue;
                }
                StringBuilder parameter = new StringBuilder(FLAG_LEFT_BRACKET);
                // 拼接参数
                for (int i = 0; i < method.getArgumentTypes().length; i++) {
                    parameter.append(method.getArgumentTypes()[i].toString());
                    if (i < method.getArgumentTypes().length - 1) {
                        parameter.append(FLAG_CAT);
                    }
                }

                parameter.append(FLAG_RIGHT_BRACKET);
                writeResult(resultWriter, FILE_KEY_METHOD_NAME_PREFIX + javaClass.getClassName() + FLAG_COLON + method.getName() + parameter + " " + maxAppNum + getAnnotation(method.getAnnotationEntries(), GRAPH_METHOD_ANNOTATION_NAME));
            }

            // 处理类的调用关系
            for (MethodCallInfoDto methodCallInfoDto : classVisitor.getMethodCallList()) {
                String data = methodCallInfoDto.getMethodCall();
                if (methodCallInfoDto.getSourceLine() != JavaCGConstants.NONE_LINE_NUMBER) {
                    if (data.contains(FLAG_ARRAY)) {
                        data = data.replace(FLAG_ARRAY, "");
                    }
                    data = data + " " + methodCallInfoDto.getSourceLine() + " " + maxAppNum;
                } else {
                    data = data + " " + maxAppNum;
                }
                writeResult(resultWriter, data);
            }
        } catch (Exception e) {
            System.err.println("处理class文件出现异常 " + jarEntryName);
            e.printStackTrace();
        }
    }

    /**
     * 获取方法上的注解
     */
    private String getAnnotation(AnnotationEntry[] annotationEntries, String filterName) {
        StringBuilder annotationText = new StringBuilder(FLAG_SPACE);
        if (annotationEntries == null || annotationEntries.length <= 0) {
            annotationText.append(ANNOTATION_NULL);
            return annotationText.toString();
        }

        for (AnnotationEntry annotationEntry : annotationEntries) {
            // 这里去判断自定义注解
            if (!annotationEntry.getAnnotationType().contains(filterName)) {
                continue;
            }
            ElementValuePair[] elementValuePairs = annotationEntry.getElementValuePairs();
            if (elementValuePairs != null && elementValuePairs.length > 0) {
                for (ElementValuePair elementValuePair : elementValuePairs) {
                    annotationText.append(elementValuePair.getValue().toString().replace(" ", ""));
                }
            }
        }

        if (annotationText.length() <= 1) {
            annotationText.append(ANNOTATION_NULL);
        }
        return annotationText.toString();
    }

    /**
     * 处理一条JarInfo
     */
    private AppInfoDto handleOneJarInfo(String jarPath, Integer flag, String version, String fileName) {
        // 获取参数
        String fullAppName = jarPath.split(COMMON_SEPARATOR)[jarPath.split(COMMON_SEPARATOR).length - 1];
        String appType = fullAppName.substring(fullAppName.lastIndexOf(FLAG_DOT) + 1);

        // 拼接入表内容 graph_app_info
        AppInfoDto appInfoDto = new AppInfoDto();
        appInfoDto.setAppName(fileName);
        appInfoDto.setAppVersion(version);
        appInfoDto.setAppType(appType);
        if (flag == 0) {
            graphGenMapper.insertAppInfo(appInfoDto);
        } else {
            graphGenMapper.insertAppInfoVersion(appInfoDto);
        }

        return appInfoDto;
    }


    // 将结果写到文件中
    private void writeResult(Writer resultWriter, String data) throws IOException {
        resultWriter.write(data + JavaCGConstants.NEW_LINE);
    }
}
