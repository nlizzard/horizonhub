package com.nlizzard.horizonhub.aspect;

import com.nlizzard.horizonhub.annotation.GlobalInterceptor;
import com.nlizzard.horizonhub.annotation.VerifyParam;
import com.nlizzard.horizonhub.constants.Constants;
import com.nlizzard.horizonhub.entity.config.WebConfig;
import com.nlizzard.horizonhub.entity.dto.SessionWebUserDto;
import com.nlizzard.horizonhub.entity.enums.ResponseCodeEnum;
import com.nlizzard.horizonhub.exception.BusinessException;
import com.nlizzard.horizonhub.service.ForumArticleService;
import com.nlizzard.horizonhub.service.ForumCommentService;
import com.nlizzard.horizonhub.service.LikeRecordService;
import com.nlizzard.horizonhub.service.UserInfoService;
import com.nlizzard.horizonhub.utils.VerifyUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Component
@Aspect
public class OperationAspect {

    private static final Logger logger = LoggerFactory.getLogger(OperationAspect.class);

    private static final String[] TYPE_BASE = {"java.lang.String", "java.lang.Integer", "java.lang.Long"};

    @Resource
    private WebConfig webConfig;

    @Resource
    private ForumArticleService forumArticleService;

    @Resource
    private ForumCommentService forumCommentService;

    @Resource
    private LikeRecordService likeRecordService;

    @Resource
    private UserInfoService userInfoService;

    // 定义切点，拦截所有被 @GlobalInterceptor 注解的方法
    @Pointcut("@annotation(com.nlizzard.horizonhub.annotation.GlobalInterceptor)")
    private void requestInterceptor() {
    }

    // 定义环绕通知，在方法执行前后进行处理
    @Around("requestInterceptor()")
    public Object interceptorDo(ProceedingJoinPoint point) throws BusinessException {
        try {
            // 获取方法实际参数值
            Object[] arguments = point.getArgs();
            // 获取方法的参数类型列表
            MethodSignature methodSignature = (MethodSignature) point.getSignature();
            Class<?>[] parameterTypes = methodSignature.getMethod().getParameterTypes();
            // 获取方法名称
            String methodName = point.getSignature().getName();
            // 获取代理对象实体
            Object target = point.getTarget();
            // 通过反射获取方法对象
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            // 获取方法上的 @GlobalInterceptor 注解
            GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);
            // 校验登录
            if (interceptor.checkLogin()) {
                checkLogin();
            }
            // 校验参数
            if (interceptor.checkParams()) {
                validateParams(method, arguments);
            }
            // TODO 校验频率


            //执行操作
            Object pointResult = point.proceed();

            // TODO 增加频次限制

            return pointResult;
        } catch (BusinessException e) {
            logger.error("全局拦截器异常", e);
            throw e;
        } catch (Throwable e) {
            logger.error("全局拦截器异常", e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }
    }

    // TODO 校验频率


    // TODO 统计已经统计数据


    /**
     * 校验登录
     */
    private void checkLogin() {
        // 拿到当前请求的session，获取用户信息，如果没有用户信息，则抛出异常
        // 1. 获取 RequestAttributes
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 2. 检查 attributes 是否为空（防止在非 Web 线程调用）
        if (attributes == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_404.getCode(), "系统异常：非 Web 请求环境");
        }
        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession();
        SessionWebUserDto sessionUser = (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);
        // TODO
//        if (sessionUser == null && webConfig.getIsDev()) { // 如果配置文件写明是开发环境，则所有接口不校验是否已登录
//            List<UserInfo> userInfoList = userInfoService.findListByParam(new UserInfoQuery());
//            if (!userInfoList.isEmpty()) {
//                UserInfo userInfo = userInfoList.get(0);
//                sessionUser = new SessionWebUserDto();
//                sessionUser.setUserId(userInfo.getUserId());
//                sessionUser.setNickName(userInfo.getNickName());
//                sessionUser.setProvince("中国");
//                sessionUser.setAdmin(true);
//                session.setAttribute(Constants.SESSION_KEY, sessionUser);
//            }
//
//        }
        // 如果session中没有用户信息，则抛出未登录业务异常
        if (null == sessionUser) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
    }

    /**
     * 参数校验 遍历参数，如果参数上有 @VerifyParam 注解，则进行校验（1.基本类型；2，对象类型）
     */
    private void validateParams(Method m, Object[] arguments) throws BusinessException {
        Parameter[] parameters = m.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object value = arguments[i];
            VerifyParam verifyParam = parameter.getAnnotation(VerifyParam.class);
            if (verifyParam == null) {
                continue;
            }
            //基本数据类型
            if (ArrayUtils.contains(TYPE_BASE, parameter.getParameterizedType().getTypeName())) {
                checkBasicTypeValue(value, verifyParam);
            } else {//如果传递的是对象
//                checkObjValue(parameter, value);
            }
        }
    }

    // TODO 校验对象参数


    /**
     * 校验基本类型参数
     *
     */
    private void checkBasicTypeValue(Object value, VerifyParam verifyParam) throws BusinessException {
        boolean isEmpty = value == null || StringUtils.isBlank(value.toString());
        int length = value == null ? 0 : value.toString().length();

        // 校验必填
        if (isEmpty && verifyParam.required()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600.getCode(), "缺少必填字段");
        }

        // 校验长度
        if (!isEmpty && (verifyParam.max() != -1 && verifyParam.max() < length || verifyParam.min() != -1 && verifyParam.min() > length)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600.getCode(), String.format("参数长度必须在 %s - %s 之间", verifyParam.min(), verifyParam.max()));
        }
        // 校验正则
        if (!isEmpty && !StringUtils.isBlank(verifyParam.regex().getRegex()) && !VerifyUtils.verify(verifyParam.regex(), String.valueOf(value))) {
            throw new BusinessException(ResponseCodeEnum.CODE_600.getCode(), verifyParam.regex().getDesc());
        }
    }
}
