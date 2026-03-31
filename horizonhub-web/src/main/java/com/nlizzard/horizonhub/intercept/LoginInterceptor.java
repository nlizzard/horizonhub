package com.nlizzard.horizonhub.intercept;

import cn.hutool.core.util.IdUtil;
import com.nlizzard.horizonhub.annotation.GlobalInterceptor;
import com.nlizzard.horizonhub.constants.Constants;
import com.nlizzard.horizonhub.entity.config.WebConfig;
import com.nlizzard.horizonhub.entity.dto.SessionWebUserDto;
import com.nlizzard.horizonhub.entity.enums.ResponseCodeEnum;
import com.nlizzard.horizonhub.entity.enums.UserStatusEnum;
import com.nlizzard.horizonhub.entity.pojo.UserInfo;
import com.nlizzard.horizonhub.entity.query.UserInfoQuery;
import com.nlizzard.horizonhub.exception.BusinessException;
import com.nlizzard.horizonhub.service.UserInfoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

import static com.nlizzard.horizonhub.constants.Constants.SESSION_KEY;

// 登录校验
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private WebConfig webConfig;

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
        if (sessionUser != null) return true;
        // 如果配置文件写明是开发环境，则所有接口不校验是否已登录
        if (webConfig.getIsDev()) {
            // 查询是否已有用于开发环境的测试账号
            UserInfoQuery userInfoQuery = new UserInfoQuery();
            userInfoQuery.setEmail(webConfig.getDevTestEmail());
            List<UserInfo> userInfoList = userInfoService.findListByParam(userInfoQuery);
            UserInfo testUser = new UserInfo();
            if (userInfoList.isEmpty()) {
                // 没有则生成测试用户
                testUser.setCurrentIntegral(10000);
                testUser.setUserId(IdUtil.getSnowflakeNextIdStr());
                testUser.setEmail(webConfig.getDevTestEmail());
                testUser.setStatus(UserStatusEnum.ENABLE.getStatus());
                testUser.setNickName("test");
                userInfoService.add(testUser);
            } else {
                testUser = userInfoList.get(0);
            }
            sessionUser = new SessionWebUserDto();
            sessionUser.setUserId(testUser.getUserId());
            sessionUser.setNickName(testUser.getNickName());
            sessionUser.setProvince("中国");
            sessionUser.setAdmin(true);
            session.setAttribute(Constants.SESSION_KEY, sessionUser);
            return true;
        }
        throw new BusinessException(ResponseCodeEnum.CODE_901);
    }
}
