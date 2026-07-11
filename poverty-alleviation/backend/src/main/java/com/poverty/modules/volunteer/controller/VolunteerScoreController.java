package com.poverty.modules.volunteer.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poverty.common.result.R;
import com.poverty.common.result.PageResult;
import com.poverty.common.utils.SecurityUtils;
import com.poverty.modules.volunteer.entity.VolunteerScore;
import com.poverty.modules.volunteer.service.VolunteerScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(tags = "志愿者积分接口")
@RestController
@RequestMapping("/api/v1/volunteer/score")
public class VolunteerScoreController {

    @Autowired
    private VolunteerScoreService volunteerScoreService;

    @ApiOperation("获取我的积分和等级")
    @GetMapping("/my")
    public R<Map<String, Object>> getMyScore() {
        Long userId = SecurityUtils.getCurrentUserId();
        Integer totalScore = volunteerScoreService.getTotalScore(userId);
        String levelName = volunteerScoreService.getUserLevelName(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("totalScore", totalScore);
        result.put("levelName", levelName);
        result.put("userId", userId);
        return R.ok(result);
    }

    @ApiOperation("分页查询积分明细")
    @GetMapping("/page")
    public R<PageResult<VolunteerScore>> getScorePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<VolunteerScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VolunteerScore::getUserId, userId);
        wrapper.orderByDesc(VolunteerScore::getCreateTime);
        IPage<VolunteerScore> pageResult = volunteerScoreService.page(new Page<>(page, size), wrapper);
        return R.ok(PageResult.of(pageResult));
    }

    @ApiOperation("志愿者积分排行榜")
    @GetMapping("/ranking")
    public R<List<Map<String, Object>>> getRanking(
            @RequestParam(defaultValue = "20") Integer limit) {
        // 查询所有积分记录，按用户分组汇总
        List<Map<String, Object>> ranking = volunteerScoreService.getBaseMapper().selectMaps(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<VolunteerScore>()
                        .select("user_id, SUM(score) as total_score")
                        .groupBy("user_id")
                        .orderByDesc("total_score")
                        .last("LIMIT " + limit)
        );
        return R.ok(ranking);
    }
}
