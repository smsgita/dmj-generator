package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;
import lombok.Data;
import picocli.CommandLine.*;
import java.util.concurrent.Callable;

@Data
@Command(name = "generate", version = "ASCIIArt 1.0", mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable {

    <#list modelConfig.models as modelInfo>

        @Option(names ={<#if modelInfo.abbr??>"-${modelInfo.abbr}"</#if>,<#if modelInfo.fieldName??>"--${modelInfo.fieldName}"</#if>}, description = "<#if modelInfo.description??>${modelInfo.description}</#if>",arity = "0..1",interactive = true,echo = true)
        private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??>= ${modelInfo.defaultValue?c}</#if>;
    </#list>

    @Override
    public Integer call() throws Exception {
    DataModel dataModel = new DataModel();
    BeanUtil.copyProperties(this,dataModel);
    MainGenerator.doGenerator(dataModel);
    return 0;
    }
}
