package com.nlizzard.horizonhub.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * AI 助手配置。
 * <p>
 * Spring AI 的 {@code OpenAiChatModel} 由 starter 自动装配（读 {@code spring.ai.openai.*}）。
 * 本类只补充「是否启用」的判断与系统提示词等业务参数。
 */
@Configuration
public class AiConfig {

    /**
     * 是否已注入 API key。未注入时（如本地未配置）AI 接口返回「未配置」提示而非报错，
     * 保证未接入 LLM 时系统其余功能不受影响。
     */
    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    @Value("${AI_BASE_URL:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${AI_MODEL:deepseek-chat}")
    private String model;

    public boolean isEnabled() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getModel() {
        return model;
    }
}
