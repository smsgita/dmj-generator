package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;
import lombok.Data;
import picocli.CommandLine;
import picocli.CommandLine.*;
import java.util.concurrent.Callable;

<#macro generatorOption indent modelInfo>
${indent}@Option(names ={<#if modelInfo.abbr??>"-${modelInfo.abbr}",</#if><#if modelInfo.fieldName??>"--${modelInfo.fieldName}"</#if>}, description = "<#if modelInfo.description??>${modelInfo.description}</#if>",arity = "0..1",interactive = true,echo = true)
${indent}private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??>= ${modelInfo.defaultValue?c}</#if>;
</#macro>

<#macro generatorCommand indent modelInfo>
${indent}System.out.println("输入${modelInfo.groupName}配置:");
${indent}CommandLine ${modelInfo.groupKey}CommandLine = new CommandLine(${modelInfo.type}Command.class);
${indent}${modelInfo.groupKey}CommandLine.execute(${modelInfo.allArgsStr});
</#macro>
@Data
@Command(name = "generate", version = "ASCIIArt 1.0", mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable {

    <#list modelConfig.models as modelInfo>

    <#if modelInfo.groupKey??>
        /**
         * ${modelInfo.groupName}
         */
        static DataModel.${modelInfo.type} ${modelInfo.groupKey} = new DataModel.${modelInfo.type}();

        @Command(name = "${modelInfo.groupName}", description = "${modelInfo.description}")
        @Data
        static class ${modelInfo.type}Command implements Runnable{

            <#list modelInfo.models as subModelInfo>
                <@generatorOption indent="           " modelInfo=subModelInfo/>
            </#list>

            @Override
            public void run(){
                <#list modelInfo.models as subModelInfo>
                ${modelInfo.groupKey}.${subModelInfo.fieldName} = ${subModelInfo.fieldName};
                </#list>
            }
        }
    <#else >
        <@generatorOption indent="        " modelInfo=modelInfo/>
    </#if>


    </#list>
    // 生成调用方法
    @Override
    public Integer call()  throws Exception {
        <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        <#if modelInfo.condition??>
        if(${modelInfo.condition}){
            <@generatorCommand indent="          " modelInfo=modelInfo></@generatorCommand>
        }
        <#else >
            <@generatorCommand indent="         " modelInfo=modelInfo/>
        </#if>

        </#if>
        </#list>
        //填充数据模型对象
        DataModel dataModel = new DataModel();
        BeanUtil.copyProperties(this,dataModel);
        <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        dataModel.${modelInfo.groupKey} = ${modelInfo.groupKey};
        </#if>
        </#list>
        MainGenerator.doGenerator(dataModel);
        return 0;
    }
}
