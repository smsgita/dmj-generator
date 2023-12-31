package com.dmj.cli.command;

import cn.hutool.core.util.ReflectUtil;
import com.dmj.model.MainTemplateConfig;
import picocli.CommandLine.Command;

import java.lang.reflect.Field;

@Command(name = "config",mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable{
    @Override
    public void run() {
        Field[] fields = ReflectUtil.getFields(MainTemplateConfig.class);
        for (Field field : fields) {
            System.out.println("字段名称"+field.getName());
            System.out.println("字段类型"+field.getType());
        }
    }
}
