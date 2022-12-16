package com.winit.graph.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * 文件/路径 相关的操作
 *
 * @Author zeyu.lin  00018326
 * @Date 12:08 2022/5/19
 */
public class GraphFileUtil {
    private static final Logger log = LoggerFactory.getLogger(GraphFileUtil.class);

    private GraphFileUtil() {
        throw new IllegalStateException("illegal");
    }

    /**
     * 判断传入的路径集合，是否都是有效的路径
     *
     * @param array jar的路径集合
     * @return true表示符合
     */
    public static boolean isValidPath(String[] array) {
        for (String jarName : array) {
            if (!new File(jarName).exists()) {
                log.error("文件或目录不存在 {}", jarName);
                return false;
            }
        }

        return true;
    }

    /**
     * 删除路径下的文件
     */
    public static boolean deleteJarAndTxt(List<String> pathList) {
        try {
            for (String path : pathList) {
                File file = new File(path);
                file.delete();
            }
            return true;
        } catch (Exception e) {
            log.error("删除文件出现异常：" + e.getMessage());
            return false;
        }
    }

    /**
     * 获取文件的目录
     */
    public static String getCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除旧的 合并包
     */
    public static boolean deleteFile(File file) {
        try {
            Files.delete(file.toPath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 通过路径，获取文件目录
     */
    public static String[] getJarArray(String jarPath, String flagSpace) {
        return jarPath.split(flagSpace);
    }

    /**
     * 判断路径是否是文件
     */
    public static boolean isValidJarPath(String[] array) {
        for (String jarName : array) {
            if (!new File(jarName).exists() || new File(jarName).isDirectory()) {
                log.error("文件不存在 {}", jarName);
                return false;
            }
        }

        return true;
    }
}
