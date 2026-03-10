package com.nlizzard.horizonhub.entity.dto;


public class SysSetting4LikeDto {
    /**
     * 点赞数量阈值
     */
    private Integer likeDayCountThreshold;

    public Integer getLikeDayCountThreshold() {
        return likeDayCountThreshold;
    }

    public void setLikeDayCountThreshold(Integer likeDayCountThreshold) {
        this.likeDayCountThreshold = likeDayCountThreshold;
    }
}
