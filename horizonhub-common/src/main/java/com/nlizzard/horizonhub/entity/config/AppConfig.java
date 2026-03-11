package com.nlizzard.horizonhub.entity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    /**
     * 是否是开发环境
     */
    @Value("${isDev}")
    private boolean isDev;

    public boolean getIsDev() {
        return isDev;
    }
}
