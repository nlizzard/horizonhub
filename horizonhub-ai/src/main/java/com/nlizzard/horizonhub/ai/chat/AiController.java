package com.nlizzard.horizonhub.ai.chat;

import com.nlizzard.horizonhub.basecontroller.BaseController;
import com.nlizzard.horizonhub.entity.dto.SessionWebUserDto;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * AI 助手接口。
 * <p>
 * 不强制登录：未登录也可用（游客可咨询论坛内容）；登录则问候个性化。
 * 登录态由 {@link BaseController#getUserInfoFromSession} 获取（兼容 Session 与 Token 双轨）。
 */
@RestController
@RequestMapping("/ai")
public class AiController extends BaseController {

    @Resource
    private AiChatService aiChatService;

    /**
     * 检测当前登录态，供前端决定是否显示登录引导。
     * 走 web 的 getUserInfoFromSession（已兼容 Token）。
     */
    @RequestMapping("/status")
    public ResponseVO<SessionWebUserDto> status(HttpSession session) {
        return getSuccessResponseVO(getUserInfoFromSession(session));
    }

    /**
     * 流式对话（SSE）。前端用 EventSource 监听。
     *
     * @param message 用户提问
     */
    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(HttpSession session,
                           @RequestParam("message") String message) {
        // 登录态仅用于个性化（非强制）；未登录 userDto 为 null，AI 正常回答
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        String finalMessage = (userDto != null && userDto.getUserId() != null)
                ? ("（当前用户：" + userDto.getNickName() + "）" + message)
                : message;
        return aiChatService.streamChat(finalMessage);
    }
}
