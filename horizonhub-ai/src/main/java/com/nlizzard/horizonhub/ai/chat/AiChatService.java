package com.nlizzard.horizonhub.ai.chat;

import com.nlizzard.horizonhub.ai.config.AiConfig;
import com.nlizzard.horizonhub.ai.context.ForumContextService;
import com.nlizzard.horizonhub.exception.BusinessException;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

/**
 * AI 对话服务：基于 Spring AI {@link ChatModel} 流式生成回答。
 * <p>
 * 检索增强：每次回答前由 {@link ForumContextService} 预取论坛相关数据（板块 + 热门/相关帖）
 * 作为上下文拼进 prompt，让 LLM 基于真实数据回答。
 * <p>
 * 流式：WebMVC 下用 {@link SseEmitter} 订阅 {@code ChatModel.stream()} 返回的 Flux，
 * 逐 token 推送给前端。
 */
@Service
public class AiChatService {

    private static final Logger logger = LoggerFactory.getLogger(AiChatService.class);

    /** SSE 超时（LLM 生成可能较慢，给 120 秒） */
    private static final long SSE_TIMEOUT = 120_000L;

    /**
     * ChatModel 可选注入：未配置 AI_API_KEY 时为 null（AiConfig 不装配该 bean），
     * 此时 AI 接口返回「未配置」提示，系统其余功能不受影响。
     */
    @Autowired(required = false)
    private ChatModel chatModel;

    @Resource
    private AiConfig aiConfig;

    @Resource
    private ForumContextService forumContextService;

    /**
     * 流式对话：返回 SseEmitter，逐 token 推送 LLM 输出。
     *
     * @param userMessage 用户提问
     * @return SSE 流
     */
    public SseEmitter streamChat(String userMessage) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        // 未配置 ChatModel（缺 AI_API_KEY）：通过 SSE 返回提示而非抛异常，前端友好展示
        if (chatModel == null) {
            try {
                emitter.send(SseEmitter.event().data("[ERROR] AI 助手未配置（缺少 AI_API_KEY）"));
            } catch (IOException e) {
                logger.warn("SSE 发送失败：{}", e.getMessage());
            }
            emitter.complete();
            return emitter;
        }

        // 组装 prompt：系统提示词 + 论坛上下文 + 用户提问
        String systemPrompt = forumContextService.buildSystemPrompt();
        String forumContext = forumContextService.buildUserContext(userMessage);
        String userContent = forumContext + "\n\n【用户提问】\n" + userMessage;

        Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userContent)
        ));

        // 注意：reactor 的 .map() 不允许返回 null（会抛 "mapper returned a null value"）。
        // 流式分帧里 result/output/text 经常为 null（如仅含元数据的帧），因此用 .handle()
        // 安全提取文本：null 时直接 next() 跳过，绝不把 null 传给下一个算子。
        chatModel.stream(prompt)
                .handle((chatResponse, sink) -> {
                    if (chatResponse == null
                            || chatResponse.getResult() == null
                            || chatResponse.getResult().getOutput() == null) {
                        return;
                    }
                    String text = chatResponse.getResult().getOutput().getText();
                    if (text != null && !text.isEmpty()) {
                        sink.next(text);
                    }
                })
                .subscribe(
                        chunk -> {
                            try {
                                emitter.send(SseEmitter.event().data(chunk));
                            } catch (IOException e) {
                                logger.warn("SSE 发送失败：{}", e.getMessage());
                                emitter.completeWithError(e);
                            }
                        },
                        error -> {
                            logger.error("AI 流式生成失败", error);
                            sendError(emitter, "AI 回复出错，请稍后重试");
                            emitter.completeWithError(error);
                        },
                        emitter::complete
                );

        // 客户端断开或超时时的兜底
        emitter.onTimeout(emitter::complete);
        emitter.onError(e -> logger.warn("SSE 连接异常：{}", e.getMessage()));

        return emitter;
    }

    private void sendError(SseEmitter emitter, String msg) {
        try {
            emitter.send(SseEmitter.event().data("[ERROR] " + msg));
        } catch (IOException ignored) {
        }
    }
}
