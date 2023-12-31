package com.dmj.generator;

import com.dmj.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        // 1.静态文件生成
        // 使用相对路径，用户使用时文件不会是固定结构
        String projectPath = System.getProperty("user.dir");
        // 输入路径 防止不同系统的路径分割符不同使用File.separator
        String inputPath = projectPath + File.separator + "generator-demo-projects" + File.separator+"acm-template";
        // 输出路径
        String outputPath = projectPath;
        StaticGenerator.copyFilesByHutool(inputPath,outputPath);

        // 2.动态文件生成
        String dynamicInputPath =projectPath + File.separator + "dmj-generator-basic" + File.separator +"src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicOutputPath = projectPath + File.separator + "acm-template/com/dmj/acm/MainTemplate.java";

        // 数据模型
        MainTemplateConfig model = new MainTemplateConfig();
        model.setAuthor("dmj");
        model.setOutputText("输出结果名称");
        model.setLoop(false);

        DynamicGenerator.doGenerator(dynamicInputPath, dynamicOutputPath, model);
    }
    public static void doGenerator(Object model) throws TemplateException, IOException {
        // 1.静态文件生成
        // 使用相对路径，用户使用时文件不会是固定结构
        String projectPath = System.getProperty("user.dir");
        // 输入路径 防止不同系统的路径分割符不同使用File.separator
        String inputPath = projectPath + File.separator + "generator-demo-projects" + File.separator+"acm-template";
        // 输出路径
        String outputPath = projectPath;
        StaticGenerator.copyFilesByHutool(inputPath,outputPath);

        // 2.动态文件生成
        String dynamicInputPath =projectPath + File.separator + "dmj-generator-basic" + File.separator +"src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicOutputPath = projectPath + File.separator + "acm-template/com/dmj/acm/MainTemplate.java";


        DynamicGenerator.doGenerator(dynamicInputPath, dynamicOutputPath, model);
    }

}
