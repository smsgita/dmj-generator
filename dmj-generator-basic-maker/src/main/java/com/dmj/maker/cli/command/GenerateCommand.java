package com.dmj.maker.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.dmj.maker.cli.command.model.DataModel;
import com.dmj.maker.generator.file.FileGenerator;
import picocli.CommandLine.*;
import java.util.concurrent.Callable;

@Command(name = "generate", version = "ASCIIArt 1.0", mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable {
    /**
     * 作者（字符串，值填充）
     */
    @Option(names = {"-a","--author"},description = "作者名称",arity = "0..1",interactive = true,echo = true)
    private String author = "dmj";

    /**
     * 输出信息
     */
    @Option(names = {"-o","--outputText"},description = "输出文本",arity = "0..1",interactive = true,echo = true)
    private String outputText = "输出结果";

    /**
     * 是否循环
     */
    @Option(names = {"-l","--loop"},description = "是否循环",arity = "0..1",interactive = true,echo = true)
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
    public Integer call() throws Exception {
        DataModel model = new DataModel();
        BeanUtil.copyProperties(this,model);
        FileGenerator.doGenerator(model);
        return 0;
    }
}
