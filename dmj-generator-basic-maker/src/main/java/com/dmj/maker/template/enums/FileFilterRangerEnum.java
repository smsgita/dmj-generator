package com.dmj.maker.template.enums;

import cn.hutool.core.util.ObjectUtil;

/**
 * 文件过滤范围枚举
 */
public enum FileFilterRangerEnum {
    FILE_NAME("动态", "fileName"),
    FILE_CONTENT("前缀匹配", "fileContent");
    private final String text;

    private final String value;

    FileFilterRangerEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }

    public static FileFilterRangerEnum getEnumByValue(String value){
        if (ObjectUtil.isEmpty(value)){
            return null;
        }
        for (FileFilterRangerEnum anEnum : FileFilterRangerEnum.values()) {
            if (anEnum.value.equals(value)){
                return anEnum;
            }
        }
        return null;
    }
}
