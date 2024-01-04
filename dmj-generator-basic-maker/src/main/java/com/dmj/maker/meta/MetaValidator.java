package com.dmj.maker.meta;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.dmj.maker.meta.enums.FileGenerateTypeEnum;
import com.dmj.maker.meta.enums.FileTypeEnum;
import com.dmj.maker.meta.enums.ModelTypeEnum;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class MetaValidator {
    public static void doValidAndFillDetail(Meta meta){
        validateAndFillDetailBase(meta);

        validAndFillDetailFileConfig(meta);

        validateAndFillDetailModelConfig(meta);
    }

    private static void validateAndFillDetailModelConfig(Meta meta) {
        // modelConfig 校验和默认值
        Meta.ModelConfig modelConfig = meta.getModelConfig();
        if (modelConfig == null){
            return;
        }
        List<Meta.ModelConfig.Models> modelInfolist = modelConfig.getModels();
        for (Meta.ModelConfig.Models modelInfo : modelInfolist) {
            if (StrUtil.isNotEmpty(modelInfo.getGroupKey())){
                // 生成中间参数、”--author“，”--outputText“
                List<Meta.ModelConfig.Models> subModelInfoList = modelInfo.getModels();
                String allArgs = subModelInfoList.stream()
                        .map(subModelInfo -> String.format("\"--%s\"", subModelInfo.getFieldName()))
                        .collect(Collectors.joining(","));
                modelInfo.setAllArgsStr(allArgs);
                continue;
            }
            String fieldName = modelInfo.getFieldName();
            if (StrUtil.isBlank(fieldName)){
                throw new MetaException("未填写 ModelConfig.Models.inputPath");
            }
            String modelInfoType = modelInfo.getType();
            if (StrUtil.isEmpty(modelInfoType)){
                modelInfo.setType(ModelTypeEnum.STRING.getValue());
            }
        }
    }

    private static void validAndFillDetailFileConfig(Meta meta) {
        // fileConfig 校验和默认值
        Meta.FileConfig fileConfig = meta.getFileConfig();
        if (fileConfig == null){
            return;
        }

        String sourceRootPath = fileConfig.getSourceRootPath();
        if (StrUtil.isBlank(sourceRootPath)){
            throw new MetaException("未填写 sourceRootPath");
        }

        String inputRootPath = fileConfig.getInputRootPath();
        if (StrUtil.isEmpty(inputRootPath)){
            // inputRootPath: .source + sourceRootPath最后一个层级目录
            String defaultInputRootPath = ".source" + File.separator +
                    FileUtil.getLastPathEle(Paths.get(sourceRootPath)).getFileName().toString();
            fileConfig.setInputRootPath(defaultInputRootPath);
        }

        String outputRootPath = fileConfig.getOutputRootPath();
        if (StrUtil.isEmpty(outputRootPath)){
            // outputRootPath: 默认为当前路径下的 generated
            String defaultOutputRootPath = "generated";
            fileConfig.setOutputRootPath(defaultOutputRootPath);
        }

        String fileConfigType = fileConfig.getType();
        if (StrUtil.isEmpty(fileConfigType)){
            fileConfig.setType(FileTypeEnum.DIR.getValue());
        }

        List<Meta.FileConfig.FileInfo> fileInfoList = fileConfig.getFiles();
        if (CollUtil.isEmpty(fileInfoList)){
            return;
        }
        for (Meta.FileConfig.FileInfo fileInfo : fileInfoList) {
            String fileInfoType = fileInfo.getType();
            if (FileTypeEnum.GROUP.getValue().equals(fileInfoType)){
                continue;
            }
            // inputPath: 必填
            String inputPath = fileInfo.getInputPath();
            if (StrUtil.isBlank(inputPath)){
                throw new MetaException("未填写 FileConfig.FileInfo.inputPath");
            }
            // outputPath: 默认等于 inputPath
            String outputPath = fileInfo.getOutputPath();
            if (StrUtil.isEmpty(outputPath)){
                fileInfo.setOutputPath(inputPath);
            }

            // fileInfoType: 默认 inputPath 有文件后缀是 file，否则是 dir
            if (StrUtil.isBlank(fileInfoType)){
                if (StrUtil.isBlank(FileUtil.getSuffix(inputPath))){
                    fileInfo.setType(FileTypeEnum.DIR.getValue());
                }else {
                    fileInfo.setType(FileTypeEnum.FILE.getValue());
                }
            }

            // generateType: 默认文件结尾为 ftl  generateType 为 dynamic 否则为 static
            String generateType = fileInfo.getGenerateType();
            if (StrUtil.isBlank(generateType)){
                if (inputPath.endsWith("ftl")){
                    fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
                }else {
                    fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
                }
            }


        }

    }

    private static void validateAndFillDetailBase(Meta meta) {
        // 校验并填充默认值
        String name = StrUtil.blankToDefault(meta.getName(), "my-generator");
        String description = StrUtil.emptyToDefault(meta.getDescription(), "我的模板代码生成器");
        String author = StrUtil.emptyToDefault(meta.getAuthor(), "dmj");
        String basePackage = StrUtil.blankToDefault(meta.getBasePackage(), "com.dmj");
        String version = StrUtil.emptyToDefault(meta.getVersion(), "1.0");
        String createTime = StrUtil.emptyToDefault(meta.getCreateTime(), DateUtil.now());
        meta.setName(name);
        meta.setDescription(description);
        meta.setAuthor(author);
        meta.setBasePackage(basePackage);
        meta.setVersion(version);
        meta.setCreateTime(createTime);
    }
}
