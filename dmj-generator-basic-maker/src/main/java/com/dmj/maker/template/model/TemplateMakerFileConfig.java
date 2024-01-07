package com.dmj.maker.template.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@Data
public class TemplateMakerFileConfig {

    private List<FileInfoConfig> fileInfoConfigList = new ArrayList<>();
    private FileGroupConfig fileGroupConfig;

    @NoArgsConstructor
    @Data
    public static class FileInfoConfig{
        private String path;
        private String condition;
        private List<FileFilterConfig> fileFilterConfigList;
    }

    @Data
    public static class FileGroupConfig{
        private String groupKey;
        private String groupName;
        private String condition;
    }
}
