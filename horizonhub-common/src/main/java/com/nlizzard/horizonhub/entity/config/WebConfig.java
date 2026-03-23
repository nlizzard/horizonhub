package com.nlizzard.horizonhub.entity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WebConfig extends AppConfig {
    /**
     * 发送人
     */
    @Value("${spring.mail.username:}")
    private String sendUserName;

    /**
     * 是否发送邮件
     */
    @Value("${send.mail.open}")
    private Boolean isSendEmailCode;

    /**
     * 获取ip省份地址查询接口
     */
    @Value("${ip.address.province.query.url}")
    private String ipAddressUrl;

    /**
     * 管理员邮箱，多个用逗号分隔
     */
    @Value("${admin.emails:}")
    private String adminEmails;

    /**
     * 管理端访问web端写入系统设置的秘钥
     */
    @Value("${inner.api.appKey}")
    private String innerApiAppKey;

    @Value("${inner.api.appSecret}")
    private String innerApiAppSecret;

    public Boolean getIsSendEmailCode() {
        return isSendEmailCode;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public String getIpAddressUrl() {
        return ipAddressUrl;
    }

    public String getAdminEmails() {
        return adminEmails;
    }

    public String getInnerApiAppKey() {
        return innerApiAppKey;
    }

    public String getInnerApiAppSecret() {
        return innerApiAppSecret;
    }
}
