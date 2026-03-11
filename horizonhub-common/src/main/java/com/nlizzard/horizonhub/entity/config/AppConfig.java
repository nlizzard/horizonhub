package com.nlizzard.horizonhub.entity.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    /**
     * 是否是开发环境
     */
    @Value("${isDev}")
    private boolean isDev;

    /**
     * 当前项目目录地址
     */
    @Value("${projectFolder}")
    private String projectFolder;

    public String getProjectFolder() {
        if (!StringUtils.isBlank(projectFolder) && !projectFolder.endsWith("/")) {
            projectFolder = projectFolder + "/";
        }
        // 如果projectFolder用的\\或\，则替换成/
        if (!StringUtils.isBlank(projectFolder) && projectFolder.contains("\\")) {
            projectFolder = projectFolder.replace("\\", "/");
        }
        return projectFolder;
    }

    public boolean getIsDev() {
        return isDev;
    }
}

