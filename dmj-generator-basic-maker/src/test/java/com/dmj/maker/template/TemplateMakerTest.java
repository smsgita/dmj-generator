package com.dmj.maker.template;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.dmj.maker.meta.Meta;
import com.dmj.maker.template.model.*;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dmj.maker.template.TemplateMaker.makeTemplate;


public class TemplateMakerTest extends TestCase {
    @Test
    public void testMakeTemplate() {
        TemplateMakerConfig templateMakerConfig = new TemplateMakerConfig();
        templateMakerConfig.setId(1l);
        templateMakerConfig.setMeta(new Meta());
        templateMakerConfig.setOriginProjectPath("文件原始路径");
        templateMakerConfig.setFileConfig(new TemplateMakerFileConfig());
        templateMakerConfig.setModelConfig(new TemplateMakerModelConfig());

        System.out.println(JSONUtil.toJsonPrettyStr(templateMakerConfig));
    }

    public void testMakeTemplateBug() {
        // 1.项目基本信息
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setName("ACM 示例模板生成器");

        // 指定原始项目目录路径
        String projectPath = System.getProperty("user.dir");
        // 要挖坑的项目的根目录
        String origenProjectPath = projectPath + File.separator + "generator-demo-projects/springboot-init";
        String fileInputPath1 = "src/main/java/com/yupi/springbootinit/common";
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("className");
        modelInfo.setType("String");

//        String searchStr = "Sum";

        String searchStr = "BaseResponse";

        // 文件过滤配置
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(fileInputPath1);

        ArrayList<FileFilterConfig> fileFilterConfigList = new ArrayList<>();
        fileInfoConfig1.setFileFilterConfigList(fileFilterConfigList);

        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = Arrays.asList(fileInfoConfig1);

        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        templateMakerFileConfig.setFileInfoConfigList(fileInfoConfigList);
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfig.setGroupKey("test");
        fileGroupConfig.setGroupName("test");
        fileGroupConfig.setCondition("test");
        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);

        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        ArrayList<TemplateMakerModelConfig.modelInfoConfig> modelInfoConfigList = new ArrayList<>();
        templateMakerModelConfig.setModelInfoConfigList(modelInfoConfigList);
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);


        TemplateMakerOutputConfig templateMakerOutputConfig = new TemplateMakerOutputConfig();
        long id = makeTemplate(meta,origenProjectPath,templateMakerFileConfig, templateMakerModelConfig, templateMakerOutputConfig, 1L);
        System.out.println(id);
    }


    public void testMakerTemplateWithJson(){
        String configStr = ResourceUtil.readUtf8Str("templateMaker.json");
        TemplateMakerConfig bean = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        System.out.println(makeTemplate(bean));
    }

    public void getTemplateJson(){

    }
}