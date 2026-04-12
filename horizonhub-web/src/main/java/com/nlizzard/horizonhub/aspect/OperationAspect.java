package com.nlizzard.horizonhub.aspect;

import com.nlizzard.horizonhub.annotation.GlobalInterceptor;
import com.nlizzard.horizonhub.annotation.VerifyParam;
import com.nlizzard.horizonhub.constants.Constants;
import com.nlizzard.horizonhub.entity.dto.SessionWebUserDto;
import com.nlizzard.horizonhub.entity.dto.SysSettingDto;
import com.nlizzard.horizonhub.entity.enums.DateTimePatternEnum;
import com.nlizzard.horizonhub.entity.enums.ResponseCodeEnum;
import com.nlizzard.horizonhub.entity.enums.UserOperFrequencyTypeEnum;
import com.nlizzard.horizonhub.entity.query.ForumArticleQuery;
import com.nlizzard.horizonhub.entity.query.ForumCommentQuery;
import com.nlizzard.horizonhub.entity.query.LikeRecordQuery;
import com.nlizzard.horizonhub.entity.vo.ResponseVO;
import com.nlizzard.horizonhub.exception.BusinessException;
import com.nlizzard.horizonhub.service.ForumArticleService;
import com.nlizzard.horizonhub.service.ForumCommentService;
import com.nlizzard.horizonhub.service.LikeRecordService;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

@Component
@Aspect
public class OperationAspect {

    private static final Logger logger = LoggerFactory.getLogger(OperationAspect.class);

    private static final String[] TYPE_BASE = {
            "java.lang.String", "java.lang.Integer", "java.lang.Long"
    };

    /**
     * Redis 中已有计数时：
     * 1. 判断是否达到上限
     * 2. 未达到则原子自增
     * 3. 设置过期时间
     * 返回值：
     * -1  表示已达到上限
     * >=0 表示自增后的最新值
     * -2  表示 key 不存在
     */
    private static final DefaultRedisScript<Long> CHECK_AND_INCREMENT_EXISTING_SCRIPT;

    /**
     * Redis key 不存在时：
     * 1. 用 DB 查询值做初始化
     * 2. 判断是否达到上限
     * 3. 未达到则原子自增
     * 返回值：
     * -1  表示已达到上限
     * >=0 表示自增后的最新值
     */
    private static final DefaultRedisScript<Long> CHECK_AND_INCREMENT_WITH_INIT_SCRIPT;

    /**
     * 业务失败时回滚预占次数
     */
    private static final DefaultRedisScript<Long> DECREMENT_SCRIPT;

    static {
        CHECK_AND_INCREMENT_EXISTING_SCRIPT = new DefaultRedisScript<>();
        CHECK_AND_INCREMENT_EXISTING_SCRIPT.setResultType(Long.class);
        CHECK_AND_INCREMENT_EXISTING_SCRIPT.setScriptText(
                "local key = KEYS[1] \n" +
                        "local limitCount = tonumber(ARGV[1]) \n" +
                        "local current = redis.call('GET', key) \n" +
                        "if (not current) then \n" +
                        "    return -2 \n" +
                        "end \n" +
                        "current = tonumber(current) \n" +
                        "if (current >= limitCount) then \n" +
                        "    return -1 \n" +
                        "end \n" +
                        "current = redis.call('INCR', key) \n" +
                        "return current"
        );

        CHECK_AND_INCREMENT_WITH_INIT_SCRIPT = new DefaultRedisScript<>();
        CHECK_AND_INCREMENT_WITH_INIT_SCRIPT.setResultType(Long.class);
        CHECK_AND_INCREMENT_WITH_INIT_SCRIPT.setScriptText(
                "local key = KEYS[1] \n" +
                        "local dbCount = tonumber(ARGV[1]) \n" +
                        "local limitCount = tonumber(ARGV[2]) \n" +
                        "local expireSeconds = tonumber(ARGV[3]) \n" +
                        "local current = redis.call('GET', key) \n" +
                        "if (not current) then \n" +
                        "    current = dbCount \n" +
                        "    redis.call('SET', key, current, 'EX', expireSeconds) \n" +
                        "else \n" +
                        "    current = tonumber(current) \n" +
                        "end \n" +
                        "if (current >= limitCount) then \n" +
                        "    return -1 \n" +
                        "end \n" +
                        "current = redis.call('INCR', key) \n" +
                        "return current"
        );

        DECREMENT_SCRIPT = new DefaultRedisScript<>();
        DECREMENT_SCRIPT.setResultType(Long.class);
        DECREMENT_SCRIPT.setScriptText(
                "local key = KEYS[1] \n" +
                        "local current = redis.call('GET', key) \n" +
                        "if (not current) then \n" +
                        "    return 0 \n" +
                        "end \n" +
                        "current = tonumber(current) \n" +
                        "if (current > 0) then \n" +
                        "    current = redis.call('DECR', key) \n" +
                        "    return 0 \n" +
                        "end \n" +
                        "return current"
        );
    }

    @Resource
    private ForumArticleService forumArticleService;

    @Resource
    private ForumCommentService forumCommentService;

    @Resource
    private LikeRecordService likeRecordService;

    @Resource
    private SysCacheUtils sysCacheUtils;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Pointcut("@annotation(com.nlizzard.horizonhub.annotation.GlobalInterceptor)")
    private void requestInterceptor() {
    }

    @Around("requestInterceptor()")
    public Object interceptorDo(ProceedingJoinPoint point) throws Throwable {
        boolean frequencyAcquired = false;
        UserOperFrequencyTypeEnum frequencyType = null;
        SessionWebUserDto webUserDto = null;

        try {
            Object[] arguments = point.getArgs();
            MethodSignature methodSignature = (MethodSignature) point.getSignature();
            Class<?>[] parameterTypes = methodSignature.getMethod().getParameterTypes();
            String methodName = point.getSignature().getName();
            Object target = point.getTarget();
            Method method = target.getClass().getMethod(methodName, parameterTypes);

            GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);

            if (interceptor.checkParams()) {
                validateParams(method, arguments);
            }

            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_500);
            }

            HttpServletRequest request = attributes.getRequest();
            HttpSession session = request.getSession(false);
            if (session != null) {
                webUserDto = (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);
            }

            frequencyType = interceptor.frequencyType();

            // 有频率限制的操作必须有登录用户
            if (needCheckFrequency(frequencyType) && webUserDto == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_600.getCode(), "用户未登录");
            }

            // 进入业务前先原子占位，防止并发穿透
            frequencyAcquired = tryAcquireFrequency(frequencyType, webUserDto);

            Object pointResult = point.proceed();

            // 只有status=success 才算真正成功
            boolean success = false;
            if (pointResult instanceof ResponseVO responseVO) {
                success = Constants.STATUS_SUCCESS.equals(responseVO.getStatus());
            }

            // 非成功结果，回滚预占次数
            if (!success && frequencyAcquired) {
                rollbackFrequencyQuietly(frequencyType, webUserDto);
            }

            return pointResult;
        } catch (Throwable e) {
            if (frequencyAcquired) {
                rollbackFrequencyQuietly(frequencyType, webUserDto);
            }
            logger.error("全局拦截器异常", e);
            throw e;
        }
    }

    /**
     * 原子校验 + 预占次数
     */
    private boolean tryAcquireFrequency(UserOperFrequencyTypeEnum typeEnum, SessionWebUserDto webUserDto) {
        if (!needCheckFrequency(typeEnum)) {
            return false;
        }

        String userId = webUserDto.getUserId();
        String curDate = DateUtils.format(new Date(), DateTimePatternEnum.YYYYMMDD.getPattern());
        String frequencyRedisKey = buildFrequencyRedisKey(userId, curDate, typeEnum);

        int limit = getLimitCount(typeEnum);
        long expireSeconds = getSecondsToTomorrow();

        // 先从redis中查
        Long result = stringRedisTemplate.execute(
                CHECK_AND_INCREMENT_EXISTING_SCRIPT,
                Collections.singletonList(frequencyRedisKey),
                String.valueOf(limit)
        );
        // 命中，但是操作次数达到上限
        if (result == -1L) {
            throw new BusinessException(ResponseCodeEnum.CODE_602.getCode(), getFrequencyLimitMsg(typeEnum, limit));
        }
        // 命中且未达到上限
        if (result != -2L) {
            return true;
        }

        // 未命中，从数据库中查操作数据
        int dbCount = getTodayCount(typeEnum, webUserDto, curDate);
        result = stringRedisTemplate.execute(
                CHECK_AND_INCREMENT_WITH_INIT_SCRIPT,
                Collections.singletonList(frequencyRedisKey),
                String.valueOf(dbCount),
                String.valueOf(limit),
                String.valueOf(expireSeconds)
        );
        // 超过上限
        if (result == -1L) {
            throw new BusinessException(ResponseCodeEnum.CODE_602.getCode(), getFrequencyLimitMsg(typeEnum, limit));
        }

        return true;
    }


    private void rollbackFrequencyQuietly(UserOperFrequencyTypeEnum typeEnum, SessionWebUserDto webUserDto) {
        try {
            rollbackFrequency(typeEnum, webUserDto);
        } catch (Exception ex) {
            logger.error("回滚频率计数失败", ex);
        }
    }

    /**
     * 业务失败时回滚预占次数
     */
    private void rollbackFrequency(UserOperFrequencyTypeEnum typeEnum, SessionWebUserDto webUserDto) {
        if (!needCheckFrequency(typeEnum) || webUserDto == null) {
            return;
        }

        String userId = webUserDto.getUserId();
        String curDate = DateUtils.format(new Date(), DateTimePatternEnum.YYYYMMDD.getPattern());
        String frequencyRedisKey = buildFrequencyRedisKey(userId, curDate, typeEnum);

        stringRedisTemplate.execute(
                DECREMENT_SCRIPT,
                Collections.singletonList(frequencyRedisKey)
        );
    }

    // 是否需要频率检测
    private boolean needCheckFrequency(UserOperFrequencyTypeEnum typeEnum) {
        return typeEnum != null && typeEnum != UserOperFrequencyTypeEnum.NO_CHECK;
    }

    // 构造redis中频率key
    private String buildFrequencyRedisKey(String userId, String curDate, UserOperFrequencyTypeEnum typeEnum) {
        return Constants.FREQUENCY_KEY + ":" + curDate + ":" + userId + ":" + typeEnum.getOperType();
    }

    // 从系统设置中拿到限额
    private int getLimitCount(UserOperFrequencyTypeEnum typeEnum) {
        SysSettingDto sysSettingDto = sysCacheUtils.getSysSetting();
        return switch (typeEnum) {
            case POST_ARTICLE -> sysSettingDto.getPostSetting().getPostDayCountThreshold();
            case POST_COMMENT -> sysSettingDto.getCommentSetting().getCommentDayCountThreshold();
            case DO_LIKE -> sysSettingDto.getLikeSetting().getLikeDayCountThreshold();
            case IMAGE_UPLOAD -> sysSettingDto.getPostSetting().getDayImageUploadCount();
            default -> Integer.MAX_VALUE;
        };
    }

    private String getFrequencyLimitMsg(UserOperFrequencyTypeEnum typeEnum, int limit) {
        return switch (typeEnum) {
            case POST_ARTICLE -> "当前系统限制每天发布文章的次数为 " + limit + " 篇";
            case POST_COMMENT -> "当前系统限制每天发布评论的次数为 " + limit + " 条";
            case DO_LIKE -> "当前系统限制每天点赞的次数为 " + limit + " 次";
            case IMAGE_UPLOAD -> "当前系统限制每天上传图片的次数为 " + limit + " 次";
            default -> "操作过于频繁";
        };
    }

    /**
     * Redis 未命中时回源数据库
     */
    private int getTodayCount(UserOperFrequencyTypeEnum typeEnum, SessionWebUserDto webUserDto, String curDate) {
        switch (typeEnum) {
            case POST_ARTICLE:
                ForumArticleQuery forumArticleQuery = new ForumArticleQuery();
                forumArticleQuery.setUserId(webUserDto.getUserId());
                forumArticleQuery.setPostTimeStart(curDate);
                forumArticleQuery.setPostTimeEnd(curDate);
                return forumArticleService.findCountByParam(forumArticleQuery);

            case POST_COMMENT:
                ForumCommentQuery forumCommentQuery = new ForumCommentQuery();
                forumCommentQuery.setUserId(webUserDto.getUserId());
                forumCommentQuery.setPostTimeStart(curDate);
                forumCommentQuery.setPostTimeEnd(curDate);
                return forumCommentService.findCountByParam(forumCommentQuery);

            case DO_LIKE:
                LikeRecordQuery recordQuery = new LikeRecordQuery();
                recordQuery.setUserId(webUserDto.getUserId());
                recordQuery.setCreateTimeStart(curDate);
                recordQuery.setCreateTimeEnd(curDate);
                return likeRecordService.findCountByParam(recordQuery);
            default:
                return 0;
        }
    }

    /**
     * 过期时间设到次日 00:00:00
     */
    private long getSecondsToTomorrow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        return Duration.between(now, tomorrow).getSeconds();
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
            Class<?> clazz = Class.forName(typeName);
            Field[] fields = clazz.getDeclaredFields();
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