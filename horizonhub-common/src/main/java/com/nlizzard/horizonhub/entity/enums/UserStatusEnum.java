package com.nlizzard.horizonhub.entity.enums;

public enum UserStatusEnum {
    ENABLE(1, "启用"),
    DISABLE(0, "禁用");


    private final Integer status;
    private final String desc;

    UserStatusEnum(Integer status, String desc) {
        this.desc = desc;
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
