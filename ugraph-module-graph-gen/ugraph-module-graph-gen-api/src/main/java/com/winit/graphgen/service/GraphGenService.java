package com.winit.graphgen.service;

public interface GraphGenService {

    /**
     * 将Jar包的内容，写入txt文件
     *
     * @param flag 0表示TRUNK 1表示version
     */
    boolean readJarWriteToFile(String jarPath, String filePath, String fileName, Integer flag, String version);

    /**
     * 读取trunk的file文件，写入数据库
     *
     * @param flag 0表示TRUNK 1表示version
     */
    boolean readFileWriteToDb(String txtFilePath, Integer flag);

}
