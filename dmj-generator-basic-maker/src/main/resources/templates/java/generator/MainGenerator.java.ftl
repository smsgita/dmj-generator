package ${basePackage}.generator;

import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;
import ${basePackage}.model.DataModel;

<#macro generatefile indent fileInfo>
${indent}inputPath = new File(inputRootPath,"${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outputRootPath,"${fileInfo.outputPath}").getAbsolutePath();
<#if fileInfo.generateType == "static">
${indent}StaticGenerator.copyFilesByHutool(inputPath,outputPath);
<#else>
${indent}DynamicGenerator.doGenerator(inputPath,outputPath,model);
</#if>
</#macro>

public class MainGenerator {
    public static void doGenerator(DataModel model) throws TemplateException, IOException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;

    <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        <#list modelInfo.models as subModelInfo>
        ${subModelInfo.type} ${subModelInfo.fieldName} = model.${modelInfo.groupKey}.${subModelInfo.fieldName};
        </#list>
        <#else>
        ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
        </#if>
    </#list>

<#list fileConfig.files as fileInfo>
    <#if fileInfo.groupKey??>
        <#if fileInfo.condition ??>
        if(${fileInfo.condition}){
        <#list fileInfo.files as fileInfo>
       <@generatefile indent="          " fileInfo=fileInfo></@generatefile>
        </#list>
        }
        <#else>
        <#list fileInfo.files as fileInfo>
        <@generatefile indent="     " fileInfo=fileInfo></@generatefile>
        </#list>
        </#if>
    <#else>
    <#if fileInfo.condition ??>
        if(${fileInfo.condition}){
        <@generatefile indent="         " fileInfo=fileInfo></@generatefile>
        }
    <#else>
        <@generatefile indent="        " fileInfo=fileInfo></@generatefile>
    </#if>
    </#if>

</#list>
    }

}
