package com.dmj.maker.template.model;

import lombok.Data;

@Data
public class TemplateMakerOutputConfig {
    // 从未分组的文件中移除分组内已存在的文件
    private boolean removeGroupFilesFromRoot = true;
}
