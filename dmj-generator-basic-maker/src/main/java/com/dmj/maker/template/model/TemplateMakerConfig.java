package com.dmj.maker.template.model;

import com.dmj.maker.meta.Meta;
import lombok.Data;

@Data
public class TemplateMakerConfig {
    private Long id;

    private Meta meta = new Meta();

    private String originProjectPath;


    private TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    private TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

    private TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();

}
