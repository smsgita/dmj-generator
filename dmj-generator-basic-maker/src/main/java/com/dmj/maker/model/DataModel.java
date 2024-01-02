package com.dmj.maker.model;

import lombok.Data;

/**
 * 静态模板配置
 */
@Data
public class DataModel {
    /**
     * 作者（字符串，值填充）
     */
    private String author = "dmj";

    /**
     * 输出信息
     */
    private String outputText = "输出结果";

    /**
     * 是否循环
     */
    private boolean loop;
}
