package com.dmj.maker.template.model;

import com.dmj.maker.meta.Meta;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class TemplateMakerModelConfig {

    private List<modelInfoConfig> modelInfoConfigList = new ArrayList<>();
    private ModelGroupConfig modelGroupConfig;

    @NoArgsConstructor
    @Data
    public static class modelInfoConfig{
        private String fieldName;
        private String type;
        private String description;
        private Object defaultValue;
        private String abbr;
        // 需要替换的文本
        private String replaceText;

    }

    @Data
    public static class ModelGroupConfig{
        private String groupKey;
        private String groupName;
        private String condition;
        private String type;
        private String description;
    }
}
