package com.nlizzard.horizonhub.aspect;

import com.nlizzard.horizonhub.annotation.GlobalInterceptor;
import com.nlizzard.horizonhub.annotation.VerifyParam;
import com.nlizzard.horizonhub.entity.enums.ResponseCodeEnum;
import com.nlizzard.horizonhub.exception.BusinessException;
import com.nlizzard.horizonhub.utils.VerifyUtils;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Component
@Aspect
public class OperationAspect {

    private static final Logger logger = LoggerFactory.getLogger(OperationAspect.class);

    private static final String[] TYPE_BASE = {"java.lang.String", "java.lang.Integer", "java.lang.Long"};


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

            // 校验参数
            if (interceptor.checkParams()) {
                validateParams(method, arguments);
            }
            //执行操作
            return point.proceed();
        } catch (BusinessException e) {
            logger.error("全局拦截器异常", e);
            throw e;
        } catch (Throwable e) {
            logger.error("全局拦截器异常", e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
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
