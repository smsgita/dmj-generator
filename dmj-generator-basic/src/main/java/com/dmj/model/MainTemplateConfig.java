package com.dmj.model;

/**
 * 静态模板配置
 */
public class MainTemplateConfig {
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    @Override
    public String toString() {
        return "MainTemplateConfig{" +
                "author='" + author + '\'' +
                ", outputText='" + outputText + '\'' +
                ", loop=" + loop +
                '}';
    }
}
