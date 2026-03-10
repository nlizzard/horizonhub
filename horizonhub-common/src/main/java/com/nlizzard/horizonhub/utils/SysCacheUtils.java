package com.nlizzard.horizonhub.utils;

import com.nlizzard.horizonhub.constants.Constants;
import com.nlizzard.horizonhub.entity.dto.SysSettingDto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统设置工具类  用于在内存中设置系统设置
 */
public class SysCacheUtils {
    // 并发安全的Map 用于存储系统设置 存储在内存中
    private final static Map<String, SysSettingDto> sysSettingMap = new ConcurrentHashMap<>();

    public static SysSettingDto getSysSetting() {
        return sysSettingMap.get(Constants.SYS_SETTING_KEY);
    }

    public static void setSysSettingMap(SysSettingDto sysSettingDto) {
        sysSettingMap.put(Constants.SYS_SETTING_KEY, sysSettingDto);
    }
}
