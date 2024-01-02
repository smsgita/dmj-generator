package com.dmj.maker.generator.file;

import cn.hutool.core.io.FileUtil;

public class StaticFileGenerator {

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
