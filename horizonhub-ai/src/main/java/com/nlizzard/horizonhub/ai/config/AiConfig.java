package com.nlizzard.horizonhub.ai.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 助手配置。
 * <p>
 * 为支持「未配置 API key 时系统仍可正常启动」（仅 AI 功能不可用），我们排除了 Spring AI 的
 * 自动配置（见 application.yml 的 spring.autoconfigure.exclude），改由本类在有 key 时
 * {@link ConditionalOnProperty 条件性}手动构建 {@link ChatModel}。
 * <ul>
 *     <li>有 {@code AI_API_KEY}：装配 {@code ChatModel}，AI 功能可用。</li>
 *     <li>无 {@code AI_API_KEY}：不装配，{@code AiChatService} 注入得到 null，AI 接口返回「未配置」提示。</li>
 * </ul>
 */
@Configuration
public class AiConfig {

    @Value("${AI_BASE_URL:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${AI_API_KEY:}")
    private String apiKey;

    @Value("${AI_MODEL:deepseek-chat}")
    private String model;

    @Value("${AI_TEMPERATURE:0.5}")
    private double temperature;

    /**
     * 是否启用 AI（是否有 key）。
     */
    public boolean isEnabled() {
        return apiKey != null && !apiKey.isBlank();
    }

    /**
     * 仅在配置了 api-key 时构建 ChatModel（使用自定义 base-url，兼容 DeepSeek/通义/Kimi 等）。
     * 与 application.yml 中对 OpenAiChatAutoConfiguration 的 exclude 配合：
     * 未配 key 时不装配此 bean，也不会触发 Spring AI 启动期校验。
     */
    @Bean
    @ConditionalOnProperty(name = "AI_API_KEY")
    public ChatModel openAiChatModel() {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();
    }
}
