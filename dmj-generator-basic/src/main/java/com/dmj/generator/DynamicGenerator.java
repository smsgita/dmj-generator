package com.dmj.generator;

import com.dmj.model.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicGenerator {

    public static void main(String[] args) throws Exception{
        String projectPath = System.getProperty("user.dir") + File.separator + "dmj-generator-basic";
        String inputPath =projectPath + File.separator +"src/main/resources/templates";

        String outputPath = projectPath + File.separator + "MainTemplate.java";
        // 数据模型
        MainTemplateConfig model = new MainTemplateConfig();
        model.setAuthor("dmj");
        model.setOutputText("输出结果名称");
        model.setLoop(false);

        doGenerator(inputPath, outputPath, model);
    }

    /**
     *
     * @param inputPath 模板文件输入路径
     * @param outputPath 输出路径
     * @param model 数据模型
     * @throws IOException
     * @throws TemplateException
     */
    public static void doGenerator(String inputPath, String outputPath,Object model) throws IOException, TemplateException {
        // new 出 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);

        File inputFile = new File(inputPath);
        File templateDir = inputFile.getParentFile();
        // 指定模板文件所在的路径
        configuration.setDirectoryForTemplateLoading(templateDir);

        // 设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");

        configuration.setNumberFormat("0.######");

        //创建模板对象，加载指定模板
        Template template = configuration.getTemplate(inputFile.getName());


        Writer out = new FileWriter(outputPath);

        template.process(model,out);
        out.close();
    }

}
