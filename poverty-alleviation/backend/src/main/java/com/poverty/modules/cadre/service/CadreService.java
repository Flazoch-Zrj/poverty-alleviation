package com.poverty.modules.cadre.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.poverty.modules.assistance.entity.AssistanceMeasure;
import com.poverty.modules.assistance.entity.AssistancePairing;
import com.poverty.modules.assistance.service.AssistanceMeasureService;
import com.poverty.modules.assistance.service.AssistancePairingService;
import com.poverty.modules.poverty.entity.FamilyMember;
import com.poverty.modules.poverty.entity.PovertyFamily;
import com.poverty.modules.poverty.service.FamilyMemberService;
import com.poverty.modules.poverty.service.PovertyFamilyService;
import com.poverty.modules.visit.entity.VisitRecord;
import com.poverty.modules.visit.service.VisitRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CadreService {

    @Autowired private PovertyFamilyService povertyFamilyService;
    @Autowired private AssistancePairingService pairingService;
    @Autowired private AssistanceMeasureService measureService;
    @Autowired private VisitRecordService visitRecordService;
    @Autowired private FamilyMemberService familyMemberService;

    public Map<String, Object> getDashboard(Long cadreUserId) {
        Map<String, Object> result = new HashMap<>();

        // 我的结对家庭
        List<AssistancePairing> pairings = pairingService.list(
                new LambdaQueryWrapper<AssistancePairing>()
                        .eq(AssistancePairing::getCadreUserId, cadreUserId)
                        .eq(AssistancePairing::getStatus, "1"));
        List<Long> familyIds = pairings.stream().map(AssistancePairing::getFamilyId).collect(Collectors.toList());
        result.put("totalFamilies", familyIds.size());

        // 待办措施（progress < 100）
        long pendingMeasures = 0;
        if (!familyIds.isEmpty()) {
            pendingMeasures = measureService.count(
                    new LambdaQueryWrapper<AssistanceMeasure>()
                            .in(AssistanceMeasure::getFamilyId, familyIds)
                            .lt(AssistanceMeasure::getProgress, 100));
        }
        result.put("pendingMeasures", pendingMeasures);

        // 本月走访
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        long monthVisits = visitRecordService.count(
                new LambdaQueryWrapper<VisitRecord>()
                        .eq(VisitRecord::getCadreUserId, cadreUserId)
                        .ge(VisitRecord::getVisitDate, monthStart));
        result.put("monthVisits", monthVisits);

        // 待审核（培训报名/就业申请等）
        result.put("pendingReviews", 0);

        // 家庭速览
        List<Map<String, Object>> familyBriefs = new ArrayList<>();
        for (Long fid : familyIds) {
            PovertyFamily family = povertyFamilyService.getById(fid);
            if (family == null) continue;
            Map<String, Object> fb = new HashMap<>();
            fb.put("familyId", fid);
            fb.put("householderName", family.getHouseholderName());
            fb.put("familyCode", family.getFamilyCode());
            fb.put("status", family.getStatus());

            // 措施平均进度
            List<AssistanceMeasure> measures = measureService.list(
                    new LambdaQueryWrapper<AssistanceMeasure>().eq(AssistanceMeasure::getFamilyId, fid));
            double avgProgress = measures.stream().mapToInt(m -> m.getProgress() != null ? m.getProgress() : 0).average().orElse(0);
            fb.put("avgProgress", Math.round(avgProgress));

            // 上次走访
            VisitRecord lastVisit = visitRecordService.getOne(
                    new LambdaQueryWrapper<VisitRecord>()
                            .eq(VisitRecord::getFamilyId, fid)
                            .orderByDesc(VisitRecord::getVisitDate)
                            .last("LIMIT 1"));
            fb.put("lastVisitDate", lastVisit != null ? lastVisit.getVisitDate() : null);
            fb.put("daysSinceVisit", lastVisit != null ? ChronoUnit.DAYS.between(lastVisit.getVisitDate(), LocalDate.now()) : null);

            familyBriefs.add(fb);
        }
        result.put("families", familyBriefs);

        // 待办事项
        List<Map<String, Object>> tasks = new ArrayList<>();
        for (Long fid : familyIds) {
            PovertyFamily family = povertyFamilyService.getById(fid);
            if (family == null) continue;
            List<AssistanceMeasure> measures = measureService.list(
                    new LambdaQueryWrapper<AssistanceMeasure>().eq(AssistanceMeasure::getFamilyId, fid));
            for (AssistanceMeasure m : measures) {
                if (m.getProgress() != null && m.getProgress() < 100) {
                    Map<String, Object> task = new HashMap<>();
                    task.put("familyId", fid);
                    task.put("familyName", family.getHouseholderName());
                    task.put("type", "measure");
                    task.put("content", m.getMeasureType() + "措施进度" + m.getProgress() + "%");
                    task.put("urgency", m.getProgress() < 30 ? "high" : "medium");
                    tasks.add(task);
                }
            }
            // 超过30天未走访
            VisitRecord lastVisit = visitRecordService.getOne(
                    new LambdaQueryWrapper<VisitRecord>()
                            .eq(VisitRecord::getFamilyId, fid)
                            .orderByDesc(VisitRecord::getVisitDate)
                            .last("LIMIT 1"));
            if (lastVisit != null && ChronoUnit.DAYS.between(lastVisit.getVisitDate(), LocalDate.now()) > 30) {
                Map<String, Object> task = new HashMap<>();
                task.put("familyId", fid);
                task.put("familyName", family.getHouseholderName());
                task.put("type", "visit");
                task.put("content", "已" + ChronoUnit.DAYS.between(lastVisit.getVisitDate(), LocalDate.now()) + "天未走访");
                task.put("urgency", "high");
                tasks.add(task);
            }
        }
        tasks.sort((a, b) -> a.get("urgency").equals("high") ? -1 : 1);
        result.put("tasks", tasks);

        return result;
    }

    public Map<String, Object> getFamilyOverview(Long familyId) {
        Map<String, Object> result = new HashMap<>();
        PovertyFamily family = povertyFamilyService.getById(familyId);
        if (family == null) return result;
        result.put("family", family);

        // 成员
        List<FamilyMember> members = familyMemberService.list(
                new LambdaQueryWrapper<FamilyMember>().eq(FamilyMember::getFamilyId, familyId));
        result.put("members", members);

        // 措施
        List<AssistanceMeasure> measures = measureService.list(
                new LambdaQueryWrapper<AssistanceMeasure>().eq(AssistanceMeasure::getFamilyId, familyId));
        result.put("measures", measures);

        // 走访
        List<VisitRecord> visits = visitRecordService.list(
                new LambdaQueryWrapper<VisitRecord>()
                        .eq(VisitRecord::getFamilyId, familyId)
                        .orderByDesc(VisitRecord::getVisitDate));
        result.put("visits", visits);

        return result;
    }

    public List<Map<String, Object>> getFamilyTimeline(Long familyId) {
        List<Map<String, Object>> timeline = new ArrayList<>();
        PovertyFamily family = povertyFamilyService.getById(familyId);
        if (family == null) return timeline;

        if (family.getFilingDate() != null) {
            timeline.add(makeEvent("建档", "家庭建档", family.getFilingDate().atStartOfDay(), "primary"));
        }

        List<AssistanceMeasure> measures = measureService.list(
                new LambdaQueryWrapper<AssistanceMeasure>().eq(AssistanceMeasure::getFamilyId, familyId));
        for (AssistanceMeasure m : measures) {
            if (m.getCreateTime() != null) {
                String label = "措施:" + m.getMeasureType();
                String desc = m.getContent() != null ? m.getContent().substring(0, Math.min(20, m.getContent().length())) + "..." : "";
                timeline.add(makeEvent(label, desc, m.getCreateTime(), "success"));
            }
        }

        List<VisitRecord> visits = visitRecordService.list(
                new LambdaQueryWrapper<VisitRecord>()
                        .eq(VisitRecord::getFamilyId, familyId)
                        .orderByDesc(VisitRecord::getVisitDate));
        for (VisitRecord v : visits) {
            if (v.getVisitDate() != null) {
                String desc = v.getContent() != null ? v.getContent().substring(0, Math.min(20, v.getContent().length())) + "..." : "";
                timeline.add(makeEvent("走访", desc, v.getVisitDate().atStartOfDay(), "warning"));
            }
        }

        timeline.sort((a, b) -> ((LocalDate) b.get("date")).compareTo((LocalDate) a.get("date")));
        return timeline;
    }

    private Map<String, Object> makeEvent(String title, String desc, java.time.LocalDateTime dateTime, String color) {
        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("description", desc);
        event.put("date", dateTime.toLocalDate());
        event.put("color", color);
        return event;
    }
}
