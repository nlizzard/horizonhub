package com.nlizzard.horizonhub.intercept;

import com.nlizzard.horizonhub.annotation.GlobalInterceptor;
import com.nlizzard.horizonhub.constants.Constants;
import com.nlizzard.horizonhub.entity.dto.SessionWebUserDto;
import com.nlizzard.horizonhub.entity.enums.ResponseCodeEnum;
import com.nlizzard.horizonhub.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.nlizzard.horizonhub.constants.Constants.SESSION_KEY;

// 登录校验
@Component
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 开发环境自动登录处理器。仅 dev profile 下存在该 Bean；生产环境为 null，
     * 因此 {@code isDev.open} 之类的布尔开关即便被误配也无法触发免登录后门。
     */
    @Autowired(required = false)
    private DevWebAutoLoginHandler devWebAutoLoginHandler;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        // 访问静态资源，直接放行。
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }
        // 没有全局校验注解直接放行
        boolean hasGlobalInterceptor = method.hasMethodAnnotation(GlobalInterceptor.class) || method.getBeanType().isAnnotationPresent(GlobalInterceptor.class);
        if (!hasGlobalInterceptor) {
            return true;
        }
        GlobalInterceptor methodAnnotation = method.getMethodAnnotation(GlobalInterceptor.class);
        // 无需检验登录，放行
        if (methodAnnotation == null || !methodAnnotation.checkLogin()) {
            return true;
        }
        // 检验登录状态
        HttpSession session = request.getSession();
        SessionWebUserDto sessionUser = (SessionWebUserDto) session.getAttribute(SESSION_KEY);
        if (sessionUser != null) {
            return true;
        }
        // 仅 dev profile 下 Bean 存在；生产环境 devWebAutoLoginHandler == null，此分支不可达
        if (devWebAutoLoginHandler != null) {
            return devWebAutoLoginHandler.createDevSession(session);
        }
        throw new BusinessException(ResponseCodeEnum.CODE_901);
    }
}
