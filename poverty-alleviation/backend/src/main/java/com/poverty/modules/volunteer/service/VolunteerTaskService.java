package com.poverty.modules.volunteer.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.poverty.modules.assistance.entity.AssistanceMeasure;
import com.poverty.modules.assistance.service.AssistanceMeasureService;
import com.poverty.modules.donation.service.DonationGoodsService;
import com.poverty.modules.need.entity.NeedsPublish;
import com.poverty.modules.need.service.NeedsPublishService;
import com.poverty.modules.poverty.entity.PovertyFamily;
import com.poverty.modules.poverty.service.PovertyFamilyService;
import com.poverty.modules.visit.entity.VisitRecord;
import com.poverty.modules.visit.service.VisitRecordService;
import com.poverty.modules.volunteer.entity.VolunteerPairing;
import com.poverty.modules.volunteer.entity.VolunteerRecord;
import com.poverty.modules.volunteer.entity.VolunteerScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VolunteerTaskService {

    @Autowired private VolunteerPairingService pairingService;
    @Autowired private NeedsPublishService needsPublishService;
    @Autowired private AssistanceMeasureService measureService;
    @Autowired private VisitRecordService visitRecordService;
    @Autowired private PovertyFamilyService povertyFamilyService;
    @Autowired private VolunteerRecordService volunteerRecordService;
    @Autowired private VolunteerScoreService volunteerScoreService;
    @Autowired private VolunteerCertificateService volunteerCertificateService;
    @Autowired private DonationGoodsService donationGoodsService;

    public List<Map<String, Object>> getTasks(Long volunteerUserId) {
        List<Map<String, Object>> tasks = new ArrayList<>();

        // 获取结对家庭 ID 列表
        List<VolunteerPairing> pairings = pairingService.list(
                new LambdaQueryWrapper<VolunteerPairing>()
                        .eq(VolunteerPairing::getVolunteerUserId, volunteerUserId)
                        .eq(VolunteerPairing::getStatus, "1"));
        List<Long> familyIds = pairings.stream().map(VolunteerPairing::getFamilyId).collect(Collectors.toList());

        for (Long familyId : familyIds) {
            PovertyFamily family = povertyFamilyService.getById(familyId);
            String familyName = family != null ? family.getHouseholderName() : "家庭#" + familyId;

            // 1. 未完成的需求
            List<NeedsPublish> needs = needsPublishService.list(
                    new LambdaQueryWrapper<NeedsPublish>()
                            .eq(NeedsPublish::getFamilyId, familyId)
                            .in(NeedsPublish::getStatus, "0", "1"));
            for (NeedsPublish n : needs) {
                Map<String, Object> t = new HashMap<>();
                t.put("id", "need-" + n.getNeedId());
                t.put("type", "need");
                t.put("title", "认领需求：" + n.getTitle());
                t.put("familyName", familyName);
                t.put("familyId", familyId);
                t.put("urgency", "high");
                t.put("link", "/volunteer/activity");
                tasks.add(t);
            }

            // 2. 进度未到 100 的措施
            List<AssistanceMeasure> measures = measureService.list(
                    new LambdaQueryWrapper<AssistanceMeasure>()
                            .eq(AssistanceMeasure::getFamilyId, familyId)
                            .lt(AssistanceMeasure::getProgress, 100));
            for (AssistanceMeasure m : measures) {
                Map<String, Object> t = new HashMap<>();
                t.put("id", "measure-" + m.getMeasureId());
                t.put("type", "measure");
                t.put("title", "协助推进：" + m.getMeasureType() + "措施（" + m.getProgress() + "%）");
                t.put("familyName", familyName);
                t.put("familyId", familyId);
                t.put("urgency", m.getProgress() != null && m.getProgress() < 30 ? "high" : "medium");
                t.put("link", "/volunteer/pairing");
                tasks.add(t);
            }

            // 3. 超过 30 天未走访
            VisitRecord lastVisit = visitRecordService.getOne(
                    new LambdaQueryWrapper<VisitRecord>()
                            .eq(VisitRecord::getFamilyId, familyId)
                            .eq(VisitRecord::getVolunteerUserId, volunteerUserId)
                            .orderByDesc(VisitRecord::getVisitDate)
                            .last("LIMIT 1"));
            long daysSinceVisit = lastVisit != null && lastVisit.getVisitDate() != null
                    ? ChronoUnit.DAYS.between(lastVisit.getVisitDate(), LocalDate.now()) : 999;
            if (daysSinceVisit > 30) {
                Map<String, Object> t = new HashMap<>();
                t.put("id", "visit-" + familyId);
                t.put("type", "visit");
                t.put("title", daysSinceVisit > 100
                        ? "🚨 已 " + daysSinceVisit + " 天未走访" + familyName + "家庭"
                        : "计划走访 " + familyName + "家庭（已 " + daysSinceVisit + " 天）");
                t.put("familyName", familyName);
                t.put("familyId", familyId);
                t.put("urgency", daysSinceVisit > 60 ? "high" : "medium");
                t.put("link", "/volunteer/pairing");
                tasks.add(t);
            }

            // 4. 待分发物资
            long pendingGoods = donationGoodsService.count(
                    new LambdaQueryWrapper<com.poverty.modules.donation.entity.DonationGoods>()
                            .eq(com.poverty.modules.donation.entity.DonationGoods::getReceiveFamilyId, familyId)
                            .eq(com.poverty.modules.donation.entity.DonationGoods::getStatus, 1));
            if (pendingGoods > 0) {
                Map<String, Object> t = new HashMap<>();
                t.put("id", "goods-" + familyId);
                t.put("type", "donation");
                t.put("title", "有 " + pendingGoods + " 项物资待分发至" + familyName + "家庭");
                t.put("familyName", familyName);
                t.put("familyId", familyId);
                t.put("urgency", "medium");
                t.put("link", "/volunteer/donation");
                tasks.add(t);
            }
        }

        // 按紧急度排序
        tasks.sort((a, b) -> {
            Map<String, Integer> order = new HashMap<>();
            order.put("high", 0); order.put("medium", 1); order.put("low", 2);
            return Integer.compare(order.getOrDefault(a.get("urgency"), 2), order.getOrDefault(b.get("urgency"), 2));
        });

        return tasks;
    }

    public Map<String, Object> getImpact(Long volunteerUserId) {
        Map<String, Object> result = new HashMap<>();

        // 结对家庭数
        long pairedFamilies = pairingService.count(
                new LambdaQueryWrapper<VolunteerPairing>()
                        .eq(VolunteerPairing::getVolunteerUserId, volunteerUserId)
                        .eq(VolunteerPairing::getStatus, "1"));
        result.put("pairedFamilies", pairedFamilies);

        // 服务记录
        List<VolunteerRecord> records = volunteerRecordService.list(
                new LambdaQueryWrapper<VolunteerRecord>()
                        .eq(VolunteerRecord::getVolunteerUserId, volunteerUserId));
        result.put("totalServices", records.size());

        double totalHours = records.stream()
                .filter(r -> r.getServiceHours() != null)
                .mapToDouble(r -> r.getServiceHours().doubleValue())
                .sum();
        result.put("totalHours", Math.round(totalHours * 10) / 10.0);

        // 完成的需求
        long completedNeeds = needsPublishService.count(
                new LambdaQueryWrapper<NeedsPublish>()
                        .eq(NeedsPublish::getVolunteerId, volunteerUserId)
                        .eq(NeedsPublish::getStatus, "2"));
        result.put("completedNeeds", completedNeeds);

        // 走访次数
        long visitCount = visitRecordService.count(
                new LambdaQueryWrapper<VisitRecord>()
                        .eq(VisitRecord::getVolunteerUserId, volunteerUserId));
        result.put("visitCount", visitCount);

        // 总积分
        List<VolunteerScore> scores = volunteerScoreService.list(
                new LambdaQueryWrapper<VolunteerScore>().eq(VolunteerScore::getUserId, volunteerUserId));
        int totalScore = scores.stream().mapToInt(VolunteerScore::getScore).sum();
        result.put("totalScore", totalScore);

        // 证书数
        long certCount = volunteerCertificateService.count(
                new LambdaQueryWrapper<com.poverty.modules.volunteer.entity.VolunteerCertificate>()
                        .eq(com.poverty.modules.volunteer.entity.VolunteerCertificate::getUserId, volunteerUserId));
        result.put("certCount", certCount);

        return result;
    }
}
