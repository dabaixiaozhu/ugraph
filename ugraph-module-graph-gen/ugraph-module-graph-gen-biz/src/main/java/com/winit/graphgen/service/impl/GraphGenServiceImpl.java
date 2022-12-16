package com.winit.graphgen.service.impl;


import com.winit.graphgen.service.GraphGenService;
import com.winit.graphgen.stat.ReadTrunkFileToDb;
import com.winit.graphgen.stat.ReadTrunkJarToFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 生成调用链相关的service
 *
 * @Author zeyu.lin  00018326
 * @Date 15:13 2022/5/23
 */
@Service
public class GraphGenServiceImpl implements GraphGenService {

    private static final Logger log = LoggerFactory.getLogger(GraphGenServiceImpl.class);

    @Autowired
    private ReadTrunkFileToDb readTrunkFileToDb;

    @Autowired
    private ReadTrunkJarToFile readTrunkJarToFile;

    @Override
    public boolean readJarWriteToFile(String jarPath, String filePath, String fileName, Integer flag, String version) {
        // 记录Java方法调用关系输出文件路径
        if (!readTrunkJarToFile.writeJarInfo(jarPath, filePath, fileName, flag, version)) {
            log.error("调用ugraph-module-graph-gen生成jar包的方法调用关系失败");
            return false;
        }

        return true;
    }

    @Override
    public boolean readFileWriteToDb(String txtFilePath, Integer flag) {
        // 读取 输出文件的内容
        return readTrunkFileToDb.handleTrunkMergedTxt(txtFilePath, flag);
    }
}
