package com.poverty.modules.volunteer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.volunteer.entity.VolunteerLevel;
import com.poverty.modules.volunteer.mapper.VolunteerLevelMapper;
import com.poverty.modules.volunteer.service.VolunteerLevelService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteerLevelServiceImpl extends ServiceImpl<VolunteerLevelMapper, VolunteerLevel> implements VolunteerLevelService {

    @Override
    public VolunteerLevel getLevelByScore(Integer totalScore) {
        List<VolunteerLevel> levels = list(new LambdaQueryWrapper<VolunteerLevel>()
                .orderByDesc(VolunteerLevel::getMinScore));
        for (VolunteerLevel level : levels) {
            if (totalScore >= level.getMinScore()) {
                return level;
            }
        }
        // 返回最低等级
        return list(new LambdaQueryWrapper<VolunteerLevel>()
                .orderByAsc(VolunteerLevel::getMinScore)
                .last("LIMIT 1"))
                .stream().findFirst().orElse(null);
    }
}
