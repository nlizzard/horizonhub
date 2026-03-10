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

    public Boolean getIsSendEmailCode() {
        return isSendEmailCode;
    }

    public String getSendUserName() {
        return sendUserName;
    }
}
