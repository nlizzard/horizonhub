package com.nlizzard.horizonhub.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.nlizzard.horizonhub.annotation.GlobalInterceptor;
import com.nlizzard.horizonhub.annotation.VerifyParam;
import com.nlizzard.horizonhub.constants.Constants;
import com.nlizzard.horizonhub.entity.config.AppConfig;
import com.nlizzard.horizonhub.entity.config.WebConfig;
import com.nlizzard.horizonhub.entity.dto.SessionWebUserDto;
import com.nlizzard.horizonhub.entity.dto.SysSettingDto;
import com.nlizzard.horizonhub.entity.enums.DateTimePatternEnum;
import com.nlizzard.horizonhub.entity.enums.ResponseCodeEnum;
import com.nlizzard.horizonhub.entity.enums.UserOperFrequencyTypeEnum;
import com.nlizzard.horizonhub.entity.enums.UserStatusEnum;
import com.nlizzard.horizonhub.entity.pojo.UserInfo;
import com.nlizzard.horizonhub.entity.query.ForumArticleQuery;
import com.nlizzard.horizonhub.entity.query.ForumCommentQuery;
import com.nlizzard.horizonhub.entity.query.LikeRecordQuery;
import com.nlizzard.horizonhub.entity.query.UserInfoQuery;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.exception.BusinessException;
import com.nlizzard.horizonhub.service.ForumArticleService;
import com.nlizzard.horizonhub.service.ForumCommentService;
import com.nlizzard.horizonhub.service.LikeRecordService;
import com.nlizzard.horizonhub.service.UserInfoService;
import com.nlizzard.horizonhub.utils.DateUtils;
import com.nlizzard.horizonhub.utils.SysCacheUtils;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.List;

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

    @Resource
    private AppConfig appConfig;

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
            // 检验频率
            this.checkFrequency(interceptor.frequencyType());

            //执行操作
            Object pointResult = point.proceed();

            // 执行完成后
            if (pointResult instanceof ResponseVO responseVO) {
                if (Constants.STATUS_SUCCESS.equals(responseVO.getStatus())) {
                    addOpCount(interceptor.frequencyType());
                }
            }
            return pointResult;
        } catch (BusinessException e) {
            logger.error("全局拦截器异常", e);
            throw e;
        } catch (Throwable e) {
            logger.error("全局拦截器异常", e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }
    }

    // 按类型检验频率 TODO 频率检查最佳实现是通过redis去做
    private void checkFrequency(UserOperFrequencyTypeEnum typeEnum) {
        if (typeEnum == null || typeEnum == UserOperFrequencyTypeEnum.NO_CHECK) {
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        SessionWebUserDto webUserDto = (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);

        String curDate = DateUtils.format(new Date(), DateTimePatternEnum.YYYY_MM_DD.getPattern());
        String sessionKey = Constants.SESSION_KEY_FREQUENCY + curDate + typeEnum;

        Integer count = (Integer) session.getAttribute(sessionKey);
        SysSettingDto sysSettingDto = SysCacheUtils.getSysSetting();
        switch (typeEnum) {
            case POST_ARTICLE:
                if (count == null) {
                    ForumArticleQuery forumArticleQuery = new ForumArticleQuery();
                    forumArticleQuery.setUserId(webUserDto.getUserId());
                    forumArticleQuery.setPostTimeStart(curDate);
                    forumArticleQuery.setPostTimeEnd(curDate);
                    count = forumArticleService.findCountByParam(forumArticleQuery);
                }
                if (count >= sysSettingDto.getPostSetting().getPostDayCountThreshold()) {
                    throw new BusinessException(ResponseCodeEnum.CODE_602.getCode(), "当前系统限制每天发布文章的次数为 " + count + " 篇");
                }
                break;
            case POST_COMMENT:
                if (count == null) {
                    ForumCommentQuery forumCommentQuery = new ForumCommentQuery();
                    forumCommentQuery.setUserId(webUserDto.getUserId());
                    forumCommentQuery.setPostTimeStart(curDate);
                    forumCommentQuery.setPostTimeEnd(curDate);
                    count = forumCommentService.findCountByParam(forumCommentQuery);
                }

                if (count >= sysSettingDto.getCommentSetting().getCommentDayCountThreshold()) {
                    throw new BusinessException(ResponseCodeEnum.CODE_602.getCode(), "当前系统限制每天发布评论的次数为 " + count + " 条");
                }
                break;
            case DO_LIKE:
                if (count == null) {
                    LikeRecordQuery recordQuery = new LikeRecordQuery();
                    recordQuery.setUserId(webUserDto.getUserId());
                    recordQuery.setCreateTimeStart(curDate);
                    recordQuery.setCreateTimeEnd(curDate);
                    count = likeRecordService.findCountByParam(recordQuery);

                }
                if (count >= sysSettingDto.getLikeSetting().getLikeDayCountThreshold()) {
                    throw new BusinessException(ResponseCodeEnum.CODE_602.getCode(), "当前系统限制每天点赞的次数为 " + count + " 次");
                }
                break;
            case IMAGE_UPLOAD: // TODO 当前实现存在问题，如果用户重新登录，会话信息会丢失，图片上传计数会失效
                if (count == null) {
                    count = 0;
                }
                if (count >= sysSettingDto.getPostSetting().getDayImageUploadCount()) {
                    throw new BusinessException(ResponseCodeEnum.CODE_602.getCode(), "当前系统限制每天上传图片的次数为 " + count + " 次");
                }
                break;
        }
        session.setAttribute(sessionKey, count);
    }

    //统计已经统计数据
    private void addOpCount(UserOperFrequencyTypeEnum typeEnum) {
        if (typeEnum == null || typeEnum == UserOperFrequencyTypeEnum.NO_CHECK) {
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        String curDate = DateUtil.format(new Date(), DateTimePatternEnum.YYYY_MM_DD.getPattern());
        String sessionKey = Constants.SESSION_KEY_FREQUENCY + curDate + typeEnum;
        Integer count = (Integer) session.getAttribute(sessionKey);
        session.setAttribute(sessionKey, count + 1);
    }


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
        // 如果配置文件写明是开发环境，则所有接口不校验是否已登录
        if (sessionUser == null && appConfig.getIsDev()) {
            // 查询是否已有用于开发环境的测试账号
            UserInfoQuery userInfoQuery = new UserInfoQuery();
            userInfoQuery.setEmail(appConfig.getDevTestEmail());
            List<UserInfo> userInfoList = userInfoService.findListByParam(userInfoQuery);
            UserInfo testUser = new UserInfo();
            if (userInfoList == null) {
                // 没有则生成测试用户
                testUser.setCurrentIntegral(10000);
                testUser.setUserId(IdUtil.getSnowflakeNextIdStr());
                testUser.setEmail(appConfig.getDevTestEmail());
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


        }
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
                checkObjValue(parameter, value);
            }
        }
    }

    // 校验对象参数
    private void checkObjValue(Parameter parameter, Object value) {
        try {
            // 通过反射获取对象的字段，遍历字段，如果字段上有 @VerifyParam 注解，则进行校验
            String typeName = parameter.getParameterizedType().getTypeName();
            Class classz = Class.forName(typeName);
            Field[] fields = classz.getDeclaredFields();
            for (Field field : fields) {
                VerifyParam fieldVerifyParam = field.getAnnotation(VerifyParam.class);
                if (fieldVerifyParam == null) {
                    continue;
                }
                field.setAccessible(true);
                Object resultValue = field.get(value);
                checkBasicTypeValue(resultValue, fieldVerifyParam);
            }
        } catch (BusinessException e) {
            logger.error("校验参数失败", e);
            throw e;
        } catch (Exception e) {
            logger.error("校验参数失败", e);
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

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
