package com.dmj.maker.generator.file;

import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class FileGenerator {
    public static void doGenerator(Object model) throws TemplateException, IOException {
        // 1.静态文件生成
        // 使用相对路径，用户使用时文件不会是固定结构
        String projectPath = System.getProperty("user.dir");
        // 输入路径 防止不同系统的路径分割符不同使用File.separator
        String inputPath = projectPath + File.separator + "generator-demo-projects" + File.separator+"acm-template";
        // 输出路径
        String outputPath = projectPath;
        StaticFileGenerator.copyFilesByHutool(inputPath,outputPath);

        // 2.动态文件生成
        String dynamicInputPath =projectPath + File.separator + "dmj-generator-basic-maker" + File.separator +"src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicOutputPath = projectPath + File.separator + "acm-template/com/dmj/acm/MainTemplate.java";


        DynamicFileGenerator.doGenerator(dynamicInputPath, dynamicOutputPath, model);
    }

}
