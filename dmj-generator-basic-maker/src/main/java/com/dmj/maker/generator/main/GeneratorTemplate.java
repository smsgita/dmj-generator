package com.dmj.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.dmj.maker.generator.JarGenerator;
import com.dmj.maker.generator.ScriptGenerator;
import com.dmj.maker.generator.file.DynamicFileGenerator;
import com.dmj.maker.meta.Meta;
import com.dmj.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class GeneratorTemplate {
    public void doGenerate() throws TemplateException, IOException, InterruptedException{
        Meta meta = MetaManager.getMetaObject();

        // 0、输出根路径
        String projectPath = System.getProperty("user.dir");
        String outputPath = projectPath + File.separator + "generated" + File.separator + meta.getName();
        if (!FileUtil.exist(outputPath)) {
            FileUtil.mkdir(outputPath);
        }

        // 1、复制原始文件
        String sourceCopyPath = copySource(meta, outputPath);

        // 2、代码生成
        generateCode(meta, outputPath);

        // 3、构建 jar 包
        String jarPath = buildJar(meta, outputPath);

        // 4、封装脚本
        String shellOutputFilePath = buildScript(outputPath, jarPath);

        // 5、生成精简版的程序（产物包）
        buildDist(outputPath, shellOutputFilePath, jarPath, sourceCopyPath);
    }

    protected  void buildDist(String outputPath, String shellOutputPath,String jarPath ,String sourceCopyPath) {
        String distOutputPath = outputPath + "-dist";
        // - 拷贝 jar 包
        String distTargetPath = distOutputPath + File.separator + "target";
        FileUtil.mkdir(distTargetPath);
        String jarSourcesPath = outputPath + File.separator + jarPath;
        FileUtil.copy(jarSourcesPath,distTargetPath,true);
        // - 拷贝脚本文件
        FileUtil.copy(shellOutputPath,distOutputPath,true);
        FileUtil.copy(shellOutputPath +".bat",distOutputPath,true);
        // - 拷贝原始模板文件
        FileUtil.copy(sourceCopyPath,distOutputPath,true);
    }

    protected  String buildScript(String outputPath,String jarPath) {
        String shellOutputPath = outputPath + File.separator + "generator";
        ScriptGenerator.doGenerator(shellOutputPath,jarPath);
        return shellOutputPath;
    }

    protected  String buildJar(Meta meta,String outputPath) throws IOException, InterruptedException {
        // 构建jar包
        JarGenerator.doGenerator(outputPath);
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        String jarPath ="target/" + jarName ;
        return jarPath;
    }

    protected  void generateCode(Meta meta, String outputPath) throws IOException, TemplateException {
        // 读取 resources 目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();

        //java 包的基础路径
        // com.dmj
        String outputBasePackage = meta.getBasePackage();
        // com/dmj
        String outPutBasePackagePath = StrUtil.join("/", StrUtil.split(outputBasePackage, "."));
        // /generated/src/main/java/com/dmj
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java/" + outPutBasePackagePath;

        String inputFilePath;
        String outputFilePath;

        // model.DataModel
        inputFilePath = inputResourcePath + File.separator + "templates/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/model/DataModel.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);

        // cli.command.ConfigCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);

        // cli.command.GenerateCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);

        // cli.command.GenerateCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/ListCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);

        // cli.CommandExecutor
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/CommandExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);

        // Main
        inputFilePath = inputResourcePath + File.separator + "templates/java/Main.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/Main.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);

        // generator.DynamicGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/DynamicGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/DynamicGenerator.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);

        // generator.MainGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/MainGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/MainGenerator.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);

        // generator.DynamicGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/StaticGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/generator/StaticGenerator.java";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);

        // pom.xml
        inputFilePath = inputResourcePath + File.separator + "templates/pom.xml.ftl";
        outputFilePath = outputPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath, meta);
    }

    protected  String copySource(Meta meta, String outputPath) {
        // 复制原始文件到代码生成器目录下的指定目录
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyPath = outputPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath,sourceCopyPath,false);
        return sourceCopyPath;
    }
}
