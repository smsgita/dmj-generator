package com.dmj.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.dmj.maker.meta.Meta;
import com.dmj.maker.meta.enums.FileGenerateTypeEnum;
import com.dmj.maker.meta.enums.FileTypeEnum;
import com.dmj.maker.template.model.*;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class TemplateMaker {
    /**
     * 制作模板
     * @param templateMakerConfig
     * @return
     */
    public static long makeTemplate(TemplateMakerConfig templateMakerConfig) {
        Long id = templateMakerConfig.getId();
        Meta meta = templateMakerConfig.getMeta();
        String originProjectPath = templateMakerConfig.getOriginProjectPath();
        TemplateMakerFileConfig templateMakerFileConfig = templateMakerConfig.getFileConfig();
        TemplateMakerModelConfig templateMakerModelConfig = templateMakerConfig.getModelConfig();
        TemplateMakerOutputConfig templateMakerOutputConfig = templateMakerConfig.getOutputConfig();

        return makeTemplate(meta,originProjectPath,templateMakerFileConfig,templateMakerModelConfig,templateMakerOutputConfig,id);
    }

    /**
     * 制作模板
     *
     * @param newMeta
     * @param origenProjectPath
     * @param templateMakerFileConfig
     * @param templateMakerModelConfig
     * @param templateMakerOutputConfig
     * @param id
     * @return
     */
    public static long makeTemplate(Meta newMeta, String origenProjectPath, TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, TemplateMakerOutputConfig templateMakerOutputConfig, Long id) {
        // 没有id则生成
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }

        // 复制项目，作为程序操作的工作空间
        String templatePath = makeWorkSpace(origenProjectPath, id);

        // 一、输入信息
        // 要挖坑的项目的根目录
        String sourceRootPath = FileUtil.loopFiles(new File(templatePath),1,null)
                .stream()
                .filter(File::isDirectory)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAbsolutePath();
        // win 系统下需要对路径进行转义
        sourceRootPath = sourceRootPath.replace("\\", "/");
        //
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = getModelInfoList(templateMakerModelConfig);
        
        // 2.输入文件信息
        List<Meta.FileConfig.FileInfo> newFileInfoList = makeFileTemplates(templateMakerFileConfig, templateMakerModelConfig, sourceRootPath);

        // 三、生成配置文件
        makeFiles(newMeta, templatePath, newFileInfoList, newModelInfoList, sourceRootPath,templateMakerOutputConfig);
        return id;
    }

    /**
     * 创建工作空间
     * @param origenProjectPath
     * @param id
     * @return
     */
    private static String makeWorkSpace(String origenProjectPath, Long id) {
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            FileUtil.copy(origenProjectPath, templatePath, true);
        }
        return templatePath;
    }

    /**
     * 文件生成
     *
     * @param newMeta
     * @param templatePath
     * @param newFileInfoList
     * @param newModelInfoList
     * @param sourceRootPath
     * @param templateMakerOutputConfig
     */
    private static void makeFiles(Meta newMeta, String templatePath, List<Meta.FileConfig.FileInfo> newFileInfoList, List<Meta.ModelConfig.ModelInfo> newModelInfoList, String sourceRootPath, TemplateMakerOutputConfig templateMakerOutputConfig) {
        // 1.构造配置参数对象
        String metaOutputPath = templatePath + File.separator + "Meta.json";


        // 已有Meta.json文件表示不是第一次制作
        if (FileUtil.exist(metaOutputPath)) {
            newMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            // 1.追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);

            List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);

            //配置去重
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));


        } else {

            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);

            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);

            fileInfoList.addAll(newFileInfoList);
            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            newMeta.setModelConfig(modelConfig);


            modelInfoList.addAll(newModelInfoList);
        }

        // 输出配置
        if (templateMakerOutputConfig != null) {
            // 文件外层和分组去重
            if (templateMakerOutputConfig.isRemoveGroupFilesFromRoot()) {
                List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
                newMeta.getFileConfig().setFiles(TemplateMakerUtils.removeGroupFilesFromRoot(fileInfoList));
            }
        }
        // 2.输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);
    }

    /**
     * 文件模板提取
     * @param templateMakerFileConfig
     * @param templateMakerModelConfig
     * @param sourceRootPath
     * @return
     */
    private static List<Meta.FileConfig.FileInfo> makeFileTemplates(TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath) {
        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = templateMakerFileConfig.getFileInfoConfigList();

        if (CollUtil.isEmpty(fileInfoConfigList)){
            return Collections.emptyList();
        }

        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        // 多文件过滤规则批量过滤
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileInfoConfigList) {
            String fileInputPath = fileInfoConfig.getPath();
            String inputFileAbsolutePath = sourceRootPath + File.separator + fileInputPath;
            // 获取按过滤配置过滤后的文件列表
            List<File> files = FileFilter.doFilter(inputFileAbsolutePath, fileInfoConfig.getFileFilterConfigList());
            // 过滤掉模板文件
            files = files.stream()
                    .filter(file -> !file.getName().endsWith("ftl"))
                    .collect(Collectors.toList());
            for (File file : files) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(templateMakerModelConfig, sourceRootPath, file,fileInfoConfig);
                newFileInfoList.add(fileInfo);
            }
        }

        // 文件分组
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if (fileGroupConfig != null) {
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();
            String condition = fileGroupConfig.getCondition();

            Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();

            groupFileInfo.setCondition(condition);
            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setGroupName(groupName);
            groupFileInfo.setFiles(newFileInfoList);

            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);

        }
        return newFileInfoList;
    }

    /**
     * 获取模型列表
     * @param templateMakerModelConfig
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> getModelInfoList(TemplateMakerModelConfig templateMakerModelConfig) {
        // 處理模型信息
        List<TemplateMakerModelConfig.modelInfoConfig> modelInfoConfigList = templateMakerModelConfig.getModelInfoConfigList();
        if (CollUtil.isEmpty(modelInfoConfigList)){
            return Collections.emptyList();
        }

        //  转换为配置文件接收的 model对象
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = modelInfoConfigList.stream().map(modelInfoConfig -> {
            Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelInfoConfig, modelInfo);
            return modelInfo;
        }).collect(Collectors.toList());

        // 本次新增的模型
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();

        // 如果是模型组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if (modelGroupConfig != null) {
            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelGroupConfig,groupModelInfo);
            // 模型全部放到一个分组里面
            groupModelInfo.setModels(inputModelInfoList);

            newModelInfoList = new ArrayList<>();
            newModelInfoList.add(groupModelInfo);

        } else {
            // 不分组添加所有的模型信息列表
            newModelInfoList.addAll(inputModelInfoList);
        }
        return newModelInfoList;
    }

    /**
     * 制作模板文件
     *
     * @param templateMakerModelConfig
     * @param sourceRootPath
     * @param inputFile
     * @return
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath, File inputFile,
                                                             TemplateMakerFileConfig.FileInfoConfig fileInfoConfig) {
        String fileInputAbsolutePath = inputFile.getAbsolutePath();
        // win 系统下需要对路径进行转义
        fileInputAbsolutePath = fileInputAbsolutePath.replace("\\", "/");
        // 要挖坑的文件 相对路径
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");

        String fileOutputPath = fileInputPath + ".ftl";


        // 二、使用字符串替换，生成模板文件
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";
        String fileContent;

        // 如果已有模板文件，表示不是第一次制作，则在原有模板上再挖坑
        // 是否已存在模板文件 true是存在 false是不存在
        boolean hasTemplateFile = FileUtil.exist(fileOutputAbsolutePath);
        if (hasTemplateFile) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);

        }

        String newFileContent = fileContent;


        // 支持多个模型：对于同一个模型文件的内容，遍历模型进行多轮替换
        List<TemplateMakerModelConfig.modelInfoConfig> modelInfoConfigList = templateMakerModelConfig.getModelInfoConfigList();
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        for (TemplateMakerModelConfig.modelInfoConfig modelInfoConfig : modelInfoConfigList) {
            String fieldName = modelInfoConfig.getFieldName();
            String replacement;
            // 模型配置
            if (modelGroupConfig == null) {
                replacement = String.format("${%s}", fieldName);
            } else {
                String groupKey = modelGroupConfig.getGroupKey();
                replacement = String.format("${%s.%s}", groupKey, fieldName);

            }
            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);

        }


        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setOutputPath(fileInputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setCondition(fileInfoConfig.getCondition());
        // 和原文件一致，没有挖坑，作为静态文件
        // 文件内容是否改变，true表示没有改变 false表示改变
        boolean isChangeContent = newFileContent.equals(fileContent);
        // 没有模板文件
        if (!hasTemplateFile) {
            // 没有模板文件，内容没改变 设为静态文件
            if (isChangeContent) {
                fileInfo.setInputPath(fileInputPath);
                fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            } else {
                // 没有模板文件，内容有改变  生成模板文件并写入内容、设为动态生成
                fileInfo.setInputPath(fileOutputPath);
                fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
                // 输出模板文件
                FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
            }
        } else if (!isChangeContent) {
            // 已有模板文件，内容有改变写入内容 没改变的不做操作
            // 输出模板文件
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }
        return fileInfo;
    }

    /**
     * 文件去重
     *
     * @param fileInfoList
     * @return
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList) {
        // 1.将所有配置文件分为有分组和无分组的

        // 先处理有分组的
        // 获取所有有分组的，将有分组的再以group进行分组
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfoList.stream()
                .filter(fileInfo -> {
                    return StrUtil.isNotBlank(fileInfo.getGroupKey());
                })
                .collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));

        // 同个group的配置合并
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
            List<Meta.FileConfig.FileInfo> tempFileList = entry.getValue();

            // 2.对于有分组的文件配置，如果有相同的分组，同组内的文件进行合并（merge），不同分组可同时保留
            List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(tempFileList.stream()
                    .flatMap(fileInfo -> fileInfo.getFiles().stream())
                    .collect(
                            Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o, (o, n) -> n)
                    ).values());

            // 使用最新的group配置
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileList);
            newFileInfo.setFiles(newFileInfoList);
            groupKeyMergedFileInfoMap.put(entry.getKey(), newFileInfo);
        }


        // 3.创建新的文件配置列表，先将合并后的文件添加到结果列表
        List<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());


        // 4.再将无分组的文件配置列表添加到结果列表
        resultList.addAll(new ArrayList<>(fileInfoList.stream()
                .filter(fileInfo -> {
                    return StrUtil.isBlank(fileInfo.getGroupKey());
                })
                .collect(
                        Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o, (o, n) -> n)
                ).values()
        ));
        return resultList;
    }

    /**
     * 模型去重
     *
     * @param modelInfoList
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList) {

        // 1.将所有配置模型分为有分组和无分组的

        // 先处理有分组的
        // 获取所有有分组的，将有分组的再以group进行分组
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeymodelInfoListMap = modelInfoList.stream()
                .filter(modelInfo -> {
                    return StrUtil.isNotBlank(modelInfo.getGroupKey());
                })
                .collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));

        // 同个group的配置合并
        Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergedmodelInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKeymodelInfoListMap.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> tempmodelList = entry.getValue();

            // 2.对于有分组的模型配置，如果有相同的分组，同组内的模型进行合并（merge），不同分组可同时保留
            List<Meta.ModelConfig.ModelInfo> newmodelInfoList = new ArrayList<>(tempmodelList.stream()
                    .flatMap(modelInfo -> modelInfo.getModels().stream())
                    .collect(
                            Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (o, n) -> n)
                    ).values());

            // 使用最新的group配置
            Meta.ModelConfig.ModelInfo newmodelInfo = CollUtil.getLast(tempmodelList);
            newmodelInfo.setModels(newmodelInfoList);
            groupKeyMergedmodelInfoMap.put(entry.getKey(), newmodelInfo);
        }


        // 3.创建新的模型配置列表，先将合并后的模型添加到结果列表
        List<Meta.ModelConfig.ModelInfo> resultList = new ArrayList<>(groupKeyMergedmodelInfoMap.values());


        // 4.再将无分组的模型配置列表添加到结果列表
        resultList.addAll(new ArrayList<>(modelInfoList.stream()
                .filter(modelInfo -> {
                    return StrUtil.isBlank(modelInfo.getGroupKey());
                })
                .collect(
                        Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (o, n) -> n)
                ).values()
        ));
        return resultList;
    }
}
