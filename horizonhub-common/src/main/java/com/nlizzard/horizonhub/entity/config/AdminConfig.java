package com.nlizzard.horizonhub.entity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminConfig extends AppConfig {
    /**
     * 管理员账号
     */
    @Value("${admin.adminName:}")
    private String adminAccount;

    /**
     * 管理员密码
     */
    @Value("${admin.password:}")
    private String adminPassword;

    @Value("${inner.api.url:}")
    private String webApiUrl;

    public String getWebApiUrl() {
        return webApiUrl;
    }

    public String getAdminAccount() {
        return adminAccount;
    }

    public String getAdminPassword() {
        return adminPassword;
    }
}
