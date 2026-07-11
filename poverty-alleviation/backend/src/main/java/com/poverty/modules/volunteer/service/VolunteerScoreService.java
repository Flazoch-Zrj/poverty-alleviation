package com.poverty.modules.volunteer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.poverty.modules.volunteer.entity.VolunteerScore;

public interface VolunteerScoreService extends IService<VolunteerScore> {

    /**
     * 记录积分并返回当前总积分
     */
    Integer addScore(Long userId, String scoreType, Integer score, Long sourceId, String sourceType, String remark);

    /**
     * 获取用户当前总积分
     */
    Integer getTotalScore(Long userId);

    /**
     * 获取用户当前等级
     */
    String getUserLevelName(Long userId);
}
