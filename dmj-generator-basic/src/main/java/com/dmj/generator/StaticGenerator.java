package com.dmj.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;

public class StaticGenerator {
    public static void main(String[] args) {
        // 使用相对路径，用户使用时文件不会是固定结构
        String projectPath = System.getProperty("user.dir");
        // 输入路径 防止不同系统的路径分割符不同使用File.separator
        String inputPath = projectPath + File.separator + "generator-demo-projects"+ File.separator+"acm-template";
        String outputPath = projectPath;
        copyFilesByHutool(inputPath,outputPath);
    }
    /**
     * 拷贝文件 (使用Hutool实现)
     * @param inputPath  输入路径
     * @param outputPath 输出路径
     */
    public static void copyFilesByHutool(String inputPath,String outputPath ){
        FileUtil.copy(inputPath,outputPath,true);
    }
    // todo 文件递归复制
}
