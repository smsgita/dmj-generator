package com.dmj.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.dmj.maker.template.enums.FileFilterRangerEnum;
import com.dmj.maker.template.enums.FileFilterRuleEnum;
import com.dmj.maker.template.model.FileFilterConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class FileFilter {

    /**
     * 对某个文件和目录进行遍历
     * @param filePath
     * @param fileFilterConfigList
     * @return
     */
    public static List<File> doFilter(String filePath,List<FileFilterConfig> fileFilterConfigList){
        List<File> fileList = FileUtil.loopFiles(filePath);
        return fileList.stream()
                .filter(file->doSingleFileFilter(fileFilterConfigList,file))
                .collect(Collectors.toList());
    }
    public static boolean doSingleFileFilter(List<FileFilterConfig> fileFilterConfigList, File file){
        String fileName = file.getName();
        String fileContent = FileUtil.readUtf8String(file);

        // 所有过滤器校验后的结果
        boolean result = true;

        if (CollUtil.isEmpty(fileFilterConfigList)){
            return true;
        }

        for (FileFilterConfig fileFilterConfig : fileFilterConfigList) {
            String range = fileFilterConfig.getRange();
            String rule = fileFilterConfig.getRule();
            String value = fileFilterConfig.getValue();

            FileFilterRangerEnum fileFilterRangerEnum = FileFilterRangerEnum.getEnumByValue(range);
            if (fileFilterRangerEnum == null){
                continue;
            }

            // 要过滤的内容
            String content = fileName;
            switch (fileFilterRangerEnum){
                case FILE_NAME:
                    content = fileName;
                    break;
                case FILE_CONTENT:
                    content = fileContent;
                    break;
            }

            FileFilterRuleEnum fileFilterRuleEnum = FileFilterRuleEnum.getEnumByValue(rule);
            if (fileFilterRuleEnum == null){
                continue;
            }
            switch (fileFilterRuleEnum) {
                case CONTENTS:
                    result = content.contains(value);
                    break;
                case STARTS_WITH:
                    result = content.startsWith(value);
                    break;
                case ENDS_WITH:
                    result = content.endsWith(value);
                    break;
                case REGEX:
                    result = content.matches(value);
                    break;
                case EQUALS:
                    result = content.equals(value);
                    break;
                default:
            }
            // 有一个不满足就返回false
            if (!result){
                return false;
            }

        }

        return result;
    }
}
