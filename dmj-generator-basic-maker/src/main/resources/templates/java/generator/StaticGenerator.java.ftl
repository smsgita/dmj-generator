package ${basePackage}.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;

public class StaticGenerator {
    /**
     * 拷贝文件 (使用Hutool实现)
     * @param inputPath  输入路径
     * @param outputPath 输出路径
     */
    public static void copyFilesByHutool(String inputPath,String outputPath ){
        FileUtil.copy(inputPath,outputPath,true);
    }
}
