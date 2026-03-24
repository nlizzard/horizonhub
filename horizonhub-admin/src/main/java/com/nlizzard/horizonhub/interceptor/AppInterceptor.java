package com.nlizzard.horizonhub.interceptor;


import com.nlizzard.horizonhub.constants.Constants;
import com.nlizzard.horizonhub.entity.config.AppConfig;
import com.nlizzard.horizonhub.entity.dto.SessionAdminUserDto;
import com.nlizzard.horizonhub.entity.enums.ResponseCodeEnum;
import com.nlizzard.horizonhub.exception.BusinessException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


// 拦截器校验登录
@Component
public class AppInterceptor implements HandlerInterceptor {

    @Resource
    private AppConfig appConfig;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        /*
          全局拦截，避免方法未设置拦截器，导致权限，日志等操作没有记录
         */
        if (request.getRequestURI().contains("checkCode") || request.getRequestURI().contains("login")) {
            return true;
        }
        checkLogin();
        return true;
    }

    /**
     *
     */
    private void checkLogin() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        SessionAdminUserDto sessionUser = (SessionAdminUserDto) session.getAttribute(Constants.SESSION_KEY);
        if (sessionUser == null && appConfig.getIsDev()) {
            sessionUser = new SessionAdminUserDto();
            sessionUser.setAccount("管理员");
            session.setAttribute(Constants.SESSION_KEY, sessionUser);
        }
        if (null == sessionUser) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}