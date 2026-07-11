package com.poverty.modules.volunteer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.poverty.modules.volunteer.entity.VolunteerLevel;

public interface VolunteerLevelService extends IService<VolunteerLevel> {

    /**
     * 根据积分获取对应等级名称
     */
    VolunteerLevel getLevelByScore(Integer totalScore);
}
