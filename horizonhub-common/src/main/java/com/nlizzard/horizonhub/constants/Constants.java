package com.nlizzard.horizonhub.constants;

public class Constants {

    // 注册赠送积分
    public static final Integer REGISTER_GIFT_INTEGRAL = 5;
    // 邮箱验证码长度
    public static final Integer EMAIL_CODE_LENGTH = 5;
    // 邮箱验证码过期时间，单位：分钟
    public static final Integer EMAIL_CODE_EXPIRED_MINUTE = 15;
    // session中登录注册的图片验证码的key
    public static final String CHECK_CODE_KEY = "check_code";
    // session中发送邮箱验证码的图片验证码的key
    public static final String CHECK_CODE_KEY_EMAIL = "check_code_email";
    // 用户ID长度
    public static final Integer USER_ID_LENGTH = 19;
    // 内存中系统设置的key，用concurrentHashMap存储
    public static final String SYS_SETTING_KEY = "sys_setting";
}
