package com.poverty.modules.volunteer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.volunteer.entity.VolunteerLevel;
import com.poverty.modules.volunteer.entity.VolunteerScore;
import com.poverty.modules.volunteer.mapper.VolunteerScoreMapper;
import com.poverty.modules.volunteer.service.VolunteerLevelService;
import com.poverty.modules.volunteer.service.VolunteerScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteerScoreServiceImpl extends ServiceImpl<VolunteerScoreMapper, VolunteerScore> implements VolunteerScoreService {

    @Autowired
    private VolunteerLevelService volunteerLevelService;

    @Override
    public Integer addScore(Long userId, String scoreType, Integer score, Long sourceId, String sourceType, String remark) {
        VolunteerScore record = new VolunteerScore();
        record.setUserId(userId);
        record.setScoreType(scoreType);
        record.setScore(score);
        record.setSourceId(sourceId);
        record.setSourceType(sourceType);
        record.setRemark(remark);
        save(record);
        return getTotalScore(userId);
    }

    @Override
    public Integer getTotalScore(Long userId) {
        LambdaQueryWrapper<VolunteerScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VolunteerScore::getUserId, userId);
        List<VolunteerScore> list = list(wrapper);
        return list.stream().mapToInt(VolunteerScore::getScore).sum();
    }

    @Override
    public String getUserLevelName(Long userId) {
        Integer totalScore = getTotalScore(userId);
        VolunteerLevel level = volunteerLevelService.getLevelByScore(totalScore);
        return level != null ? level.getLevelName() : "一星志愿者";
    }
}
