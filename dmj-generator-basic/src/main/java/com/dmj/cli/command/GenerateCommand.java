package com.dmj.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.dmj.generator.MainGenerator;
import com.dmj.model.MainTemplateConfig;
import lombok.Data;
import picocli.CommandLine.*;
import java.util.concurrent.Callable;

@Data
@Command(name = "generate", version = "1.0", mixinStandardHelpOptions = true)
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

    @Override
    public Integer call() throws Exception {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        BeanUtil.copyProperties(this,mainTemplateConfig);
        MainGenerator.doGenerator(mainTemplateConfig);
        return 0;
    }
}
