package com.poverty.modules.volunteer.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poverty.common.result.R;
import com.poverty.common.result.PageResult;
import com.poverty.common.utils.SecurityUtils;
import com.poverty.modules.assistance.entity.AssistanceMeasure;
import com.poverty.modules.assistance.service.AssistanceMeasureService;
import com.poverty.modules.poverty.service.PovertyFamilyService;
import com.poverty.modules.volunteer.entity.VolunteerActivity;
import com.poverty.modules.volunteer.entity.VolunteerRecord;
import com.poverty.modules.volunteer.service.VolunteerActivityService;
import com.poverty.modules.volunteer.service.VolunteerCertificateService;
import com.poverty.modules.volunteer.service.VolunteerRecordService;
import com.poverty.modules.volunteer.service.VolunteerScoreService;
import com.poverty.modules.volunteer.service.VolunteerTaskService;
import com.poverty.modules.volunteer.service.VolunteerPairingService;
import com.poverty.modules.volunteer.service.VolunteerNotificationService;
import com.poverty.modules.volunteer.entity.VolunteerPairing;
import com.poverty.modules.volunteer.entity.VolunteerNotification;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Api(tags = "志愿服务接口")
@RestController
@RequestMapping("/api/v1/volunteer")
public class VolunteerController {

    @Autowired
    private VolunteerActivityService volunteerActivityService;

    @Autowired
    private VolunteerRecordService volunteerRecordService;

    @Autowired
    private VolunteerScoreService volunteerScoreService;

    @Autowired
    private VolunteerCertificateService volunteerCertificateService;

    @Autowired
    private AssistanceMeasureService assistanceMeasureService;

    @Autowired
    private PovertyFamilyService povertyFamilyService;

    @Autowired
    private VolunteerPairingService volunteerPairingService;

    @Autowired
    private VolunteerTaskService volunteerTaskService;

    @Autowired
    private VolunteerNotificationService volunteerNotificationService;
    // ==================== 活动管理 ====================

    @ApiOperation("分页查询活动列表")
    @GetMapping("/activity/page")
    public R<PageResult<VolunteerActivity>> getActivityPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String activityType,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long familyId) {
        LambdaQueryWrapper<VolunteerActivity> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) wrapper.like(VolunteerActivity::getTitle, keyword);
        if (activityType != null && !activityType.isEmpty()) wrapper.eq(VolunteerActivity::getActivityType, activityType);
        if (status != null) wrapper.eq(VolunteerActivity::getStatus, status);
        if (familyId != null) wrapper.eq(VolunteerActivity::getFamilyId, familyId);
        wrapper.orderByDesc(VolunteerActivity::getCreateTime);
        IPage<VolunteerActivity> pageResult = volunteerActivityService.page(new Page<>(page, size), wrapper);
        // 填充户主姓名
        for (VolunteerActivity act : pageResult.getRecords()) {
            if (act.getFamilyId() != null) {
                com.poverty.modules.poverty.entity.PovertyFamily f = povertyFamilyService.getById(act.getFamilyId());
                if (f != null) act.setFamilyName(f.getHouseholderName());
            }
        }
        return R.ok(PageResult.of(pageResult));
    }

    @ApiOperation("获取活动详情")
    @GetMapping("/activity/{id}")
    public R<VolunteerActivity> getActivityById(@PathVariable Long id) {
        return R.ok(volunteerActivityService.getById(id));
    }

    @ApiOperation("新增活动（关联家庭时自动创建帮扶措施）")
    @PostMapping("/activity")
    public R<Void> addActivity(@RequestBody VolunteerActivity volunteerActivity) {
        if (volunteerActivity.getStatus() == null) volunteerActivity.setStatus(1);
        if (volunteerActivity.getOrganizerId() == null) volunteerActivity.setOrganizerId(SecurityUtils.getCurrentUserId());
        volunteerActivityService.save(volunteerActivity);

        // 如果活动关联了家庭，自动创建帮扶措施
        if (volunteerActivity.getFamilyId() != null) {
            AssistanceMeasure measure = new AssistanceMeasure();
            measure.setFamilyId(volunteerActivity.getFamilyId());
            measure.setMeasureType("志愿帮扶");
            measure.setContent(volunteerActivity.getTitle());
            measure.setStatus("1");
            measure.setProgress(0);
            assistanceMeasureService.save(measure);
        }
        return R.ok();
    }

    @ApiOperation("修改活动")
    @PutMapping("/activity/{id}")
    public R<Void> updateActivity(@PathVariable Long id, @RequestBody VolunteerActivity volunteerActivity) {
        volunteerActivity.setActivityId(id);
        volunteerActivityService.updateById(volunteerActivity);
        return R.ok();
    }

    @ApiOperation("删除活动")
    @DeleteMapping("/activity/{id}")
    public R<Void> deleteActivity(@PathVariable Long id) {
        volunteerActivityService.removeById(id);
        return R.ok();
    }

    // ==================== 服务记录 ====================

    @ApiOperation("新增志愿服务记录（报名 + 增加已报名人数）")
    @PostMapping("/record")
    public R<Void> addRecord(@RequestBody VolunteerRecord volunteerRecord) {
        if (volunteerRecord.getVolunteerUserId() == null) volunteerRecord.setVolunteerUserId(SecurityUtils.getCurrentUserId());
        if (volunteerRecord.getStatus() == null) volunteerRecord.setStatus(1);
        // 如果活动关联了家庭，自动填充 family_id
        if (volunteerRecord.getFamilyId() == null && volunteerRecord.getActivityId() != null) {
            VolunteerActivity activity = volunteerActivityService.getById(volunteerRecord.getActivityId());
            if (activity != null && activity.getFamilyId() != null) {
                volunteerRecord.setFamilyId(activity.getFamilyId());
            }
        }
        volunteerRecordService.save(volunteerRecord);
        VolunteerActivity activity = volunteerActivityService.getById(volunteerRecord.getActivityId());
        if (activity != null) {
            activity.setRegisteredCount(activity.getRegisteredCount() + 1);
            volunteerActivityService.updateById(activity);
        }
        return R.ok();
    }

    @ApiOperation("分页查询志愿服务记录")
    @GetMapping("/record/page")
    public R<PageResult<VolunteerRecord>> getRecordPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long activityId,
            @RequestParam(required = false) Long volunteerUserId,
            @RequestParam(required = false) Long familyId) {
        LambdaQueryWrapper<VolunteerRecord> wrapper = new LambdaQueryWrapper<>();
        if (activityId != null) wrapper.eq(VolunteerRecord::getActivityId, activityId);
        if (volunteerUserId != null) wrapper.eq(VolunteerRecord::getVolunteerUserId, volunteerUserId);
        if (familyId != null) wrapper.eq(VolunteerRecord::getFamilyId, familyId);
        wrapper.orderByDesc(VolunteerRecord::getSignInTime);
        IPage<VolunteerRecord> pageResult = volunteerRecordService.page(new Page<>(page, size), wrapper);
        return R.ok(PageResult.of(pageResult));
    }

    @ApiOperation("签到")
    @PutMapping("/record/{id}/sign-in")
    public R<Void> signIn(@PathVariable Long id) {
        VolunteerRecord record = new VolunteerRecord();
        record.setRecordId(id);
        record.setSignInTime(LocalDateTime.now());
        record.setStatus(2);
        volunteerRecordService.updateById(record);
        return R.ok();
    }

    @ApiOperation("签退（计算服务时长 + 自动积分 + 更新帮扶进度）")
    @PutMapping("/record/{id}/sign-out")
    public R<Void> signOut(@PathVariable Long id) {
        VolunteerRecord existing = volunteerRecordService.getById(id);
        if (existing != null && existing.getSignInTime() != null) {
            LocalDateTime now = LocalDateTime.now();
            long minutes = Duration.between(existing.getSignInTime(), now).toMinutes();
            double hours = minutes / 60.0;

            VolunteerRecord record = new VolunteerRecord();
            record.setRecordId(id);
            record.setSignOutTime(now);
            record.setServiceHours(BigDecimal.valueOf(hours));
            record.setStatus(3);
            volunteerRecordService.updateById(record);

            // 自动计算积分
            int scorePoints = Math.max(1, (int) Math.ceil(hours * 10));
            VolunteerActivity activity = volunteerActivityService.getById(existing.getActivityId());
            if (activity != null && activity.getDifficulty() != null) {
                scorePoints = (int) (scorePoints * (1 + (activity.getDifficulty() - 1) * 0.25));
            }
            volunteerScoreService.addScore(existing.getVolunteerUserId(), "service_hour", scorePoints, id, "activity",
                    "志愿服务 " + String.format("%.1f", hours) + " 小时");

            // 自动更新帮扶措施进度
            if (activity != null && activity.getFamilyId() != null) {
                LambdaQueryWrapper<AssistanceMeasure> mw = new LambdaQueryWrapper<>();
                mw.eq(AssistanceMeasure::getFamilyId, activity.getFamilyId());
                mw.eq(AssistanceMeasure::getMeasureType, "志愿帮扶");
                mw.orderByDesc(AssistanceMeasure::getMeasureId).last("LIMIT 1");
                AssistanceMeasure measure = assistanceMeasureService.getOne(mw);
                if (measure != null) {
                    int newProgress = Math.min(100, (measure.getProgress() != null ? measure.getProgress() : 0) + (int) Math.ceil(hours * 10));
                    measure.setProgress(newProgress);
                    if (newProgress >= 100) measure.setStatus("2");
                    assistanceMeasureService.updateById(measure);
                }
            }

            // 自动颁发证书
            Long userId = existing.getVolunteerUserId();
            Integer totalScore = volunteerScoreService.getTotalScore(userId);
            String certName = null;
            if (totalScore >= 1000) certName = "五星志愿者证书（累计1000+积分）";
            else if (totalScore >= 600) certName = "四星志愿者证书（累计600+积分）";
            else if (totalScore >= 300) certName = "三星志愿者证书（累计300+积分）";
            else if (totalScore >= 100) certName = "二星志愿者证书（累计100+积分）";
            if (certName != null) volunteerCertificateService.issueCertificate(userId, "service_hours", null, certName);
        }
        return R.ok();
    }

    @ApiOperation("提交受益方评价")
    @PutMapping("/record/{id}/feedback")
    public R<Void> submitFeedback(@PathVariable Long id,
                                   @RequestParam(required = false) String feedback,
                                   @RequestParam Integer rating) {
        VolunteerRecord record = volunteerRecordService.getById(id);
        if (record != null) {
            record.setBeneficiaryFeedback(feedback);
            record.setBeneficiaryRating(rating);
            record.setStatus(4);
            volunteerRecordService.updateById(record);
            int ratingBonus = rating * 4;
            volunteerScoreService.addScore(record.getVolunteerUserId(), "rating_bonus", ratingBonus, id, "activity",
                    "受益方评价 " + rating + " 星");
        }
        return R.ok();
    }

    // ==================== 结对管理 ====================

    @ApiOperation("我的结对家庭列表")
    @GetMapping("/pairing/my")
    public R<List<Map<String, Object>>> getMyPairings() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<VolunteerPairing> pairings = volunteerPairingService.list(
                new LambdaQueryWrapper<VolunteerPairing>()
                        .eq(VolunteerPairing::getVolunteerUserId, userId)
                        .eq(VolunteerPairing::getStatus, "1"));
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (VolunteerPairing p : pairings) {
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("pairing", p);
            com.poverty.modules.poverty.entity.PovertyFamily family = povertyFamilyService.getById(p.getFamilyId());
            if (family != null) {
                item.put("family", family);
                item.put("familyName", family.getHouseholderName());
                long serviceCount = volunteerRecordService.count(
                        new LambdaQueryWrapper<VolunteerRecord>()
                                .eq(VolunteerRecord::getFamilyId, p.getFamilyId())
                                .eq(VolunteerRecord::getVolunteerUserId, userId));
                item.put("serviceCount", serviceCount);
            }
            result.add(item);
        }
        return R.ok(result);
    }

    @ApiOperation("结对家庭全息视图")
    @GetMapping("/pairing/family/{familyId}/dashboard")
    public R<Map<String, Object>> familyDashboard(@PathVariable Long familyId) {
        Map<String, Object> result = new java.util.HashMap<>();
        com.poverty.modules.poverty.entity.PovertyFamily family = povertyFamilyService.getById(familyId);
        if (family == null) return R.fail("家庭不存在");
        result.put("family", family);

        List<AssistanceMeasure> measures = assistanceMeasureService.list(
                new LambdaQueryWrapper<AssistanceMeasure>().eq(AssistanceMeasure::getFamilyId, familyId));
        result.put("measures", measures);

        List<VolunteerRecord> myRecords = volunteerRecordService.list(
                new LambdaQueryWrapper<VolunteerRecord>()
                        .eq(VolunteerRecord::getFamilyId, familyId)
                        .eq(VolunteerRecord::getVolunteerUserId, SecurityUtils.getCurrentUserId())
                        .orderByDesc(VolunteerRecord::getSignInTime));
        result.put("myRecords", myRecords);

        return R.ok(result);
    }

    @ApiOperation("我的待办任务")
    @GetMapping("/tasks")
    public R<List<Map<String, Object>>> getMyTasks() {
        return R.ok(volunteerTaskService.getTasks(SecurityUtils.getCurrentUserId()));
    }

    @ApiOperation("我的帮扶影响力")
    @GetMapping("/impact")
    public R<Map<String, Object>> getMyImpact() {
        return R.ok(volunteerTaskService.getImpact(SecurityUtils.getCurrentUserId()));
    }

    // ==================== 通知系统 ====================

    @ApiOperation("未读通知数")
    @GetMapping("/notifications/unread-count")
    public R<Map<String, Object>> unreadCount() {
        Map<String, Object> r = new java.util.HashMap<>();
        r.put("count", volunteerNotificationService.getUnreadCount(SecurityUtils.getCurrentUserId()));
        return R.ok(r);
    }

    @ApiOperation("通知列表")
    @GetMapping("/notifications")
    public R<List<VolunteerNotification>> getNotifications(@RequestParam(defaultValue = "20") Integer limit) {
        return R.ok(volunteerNotificationService.getMyNotifications(SecurityUtils.getCurrentUserId(), limit));
    }

    @ApiOperation("标记已读")
    @PutMapping("/notifications/{id}/read")
    public R<Void> markRead(@PathVariable Long id) {
        volunteerNotificationService.markAsRead(id);
        return R.ok();
    }

    @ApiOperation("全部标记已读")
    @PutMapping("/notifications/read-all")
    public R<Void> markAllRead() {
        volunteerNotificationService.markAllAsRead(SecurityUtils.getCurrentUserId());
        return R.ok();
    }
}
