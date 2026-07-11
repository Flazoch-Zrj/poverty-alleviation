package com.poverty.modules.dashboard.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.poverty.common.result.R;
import com.poverty.modules.assistance.entity.AssistanceMeasure;
import com.poverty.modules.assistance.service.AssistanceMeasureService;
import com.poverty.modules.assistance.service.AssistancePairingService;
import com.poverty.modules.donation.service.DonationMoneyService;
import com.poverty.modules.job.service.JobApplicationService;
import com.poverty.modules.need.entity.NeedsPublish;
import com.poverty.modules.need.service.NeedsPublishService;
import com.poverty.modules.poverty.entity.PovertyFamily;
import com.poverty.modules.poverty.service.PovertyFamilyService;
import com.poverty.modules.risk.service.RiskService;
import com.poverty.modules.training.service.TrainingEnrollmentService;
import com.poverty.modules.visit.service.VisitRecordService;
import com.poverty.modules.volunteer.entity.VolunteerRecord;
import com.poverty.modules.volunteer.service.VolunteerRecordService;
import com.poverty.modules.volunteer.service.VolunteerScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "数据大屏接口")
@RestController
@RequestMapping("/api/v1/dashboard/big-screen")
@PreAuthorize("hasRole('admin')")
public class BigScreenController {

    @Autowired private PovertyFamilyService povertyFamilyService;
    @Autowired private AssistancePairingService pairingService;
    @Autowired private AssistanceMeasureService measureService;
    @Autowired private VisitRecordService visitRecordService;
    @Autowired private DonationMoneyService donationMoneyService;
    @Autowired private NeedsPublishService needsPublishService;
    @Autowired private VolunteerRecordService volunteerRecordService;
    @Autowired private VolunteerScoreService volunteerScoreService;
    @Autowired private TrainingEnrollmentService trainingEnrollmentService;
    @Autowired private JobApplicationService jobApplicationService;

    @ApiOperation("大屏总览数据")
    @GetMapping("/overview")
    public R<Map<String, Object>> overview() {
        Map<String, Object> data = new HashMap<>();

        // 1. 核心指标
        long totalFamilies = povertyFamilyService.count();
        long pairedFamilies = pairingService.count();
        long visitedThisMonth = visitRecordService.count(new LambdaQueryWrapper<com.poverty.modules.visit.entity.VisitRecord>()
                .ge(com.poverty.modules.visit.entity.VisitRecord::getVisitDate, LocalDate.now().withDayOfMonth(1)));
        Double totalDonation = donationMoneyService.getBaseMapper().selectObjs(
                new QueryWrapper<com.poverty.modules.donation.entity.DonationMoney>()
                        .select("IFNULL(SUM(amount),0)").eq("status", 1)
        ).stream().mapToDouble(o -> Double.parseDouble(o.toString())).sum();

        Map<String, Object> core = new HashMap<>();
        core.put("totalFamilies", totalFamilies);
        core.put("pairedFamilies", pairedFamilies);
        core.put("visitThisMonth", visitedThisMonth);
        core.put("totalDonation", totalDonation);
        core.put("pairRate", totalFamilies > 0 ? Math.round(pairedFamilies * 100.0 / totalFamilies) : 0);
        data.put("core", core);

        // 2. 脱贫状态分布
        long normal = povertyFamilyService.count(new LambdaQueryWrapper<PovertyFamily>().eq(PovertyFamily::getStatus, "建档"));
        long lifted = povertyFamilyService.count(new LambdaQueryWrapper<PovertyFamily>().eq(PovertyFamily::getStatus, "已脱贫"));
        long returned = povertyFamilyService.count(new LambdaQueryWrapper<PovertyFamily>().eq(PovertyFamily::getStatus, "已返贫"));
        Map<String, Object> n1 = new HashMap<>(); n1.put("name", "建档"); n1.put("value", normal);
        Map<String, Object> n2 = new HashMap<>(); n2.put("name", "已脱贫"); n2.put("value", lifted);
        Map<String, Object> n3 = new HashMap<>(); n3.put("name", "已返贫"); n3.put("value", returned);
        data.put("statusDist", Arrays.asList(n1, n2, n3));

        // 3. 月度走访趋势（近6个月）
        List<Map<String, Object>> visitTrend = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate month = LocalDate.now().minusMonths(i);
            LocalDate start = month.withDayOfMonth(1);
            LocalDate end = month.withDayOfMonth(month.lengthOfMonth());
            long count = visitRecordService.count(new LambdaQueryWrapper<com.poverty.modules.visit.entity.VisitRecord>()
                    .ge(com.poverty.modules.visit.entity.VisitRecord::getVisitDate, start)
                    .le(com.poverty.modules.visit.entity.VisitRecord::getVisitDate, end));
            Map<String, Object> m = new HashMap<>();
            m.put("month", month.getMonthValue() + "月");
            m.put("count", count);
            visitTrend.add(m);
        }
        data.put("visitTrend", visitTrend);

        // 4. 措施类型分布
        List<AssistanceMeasure> allMeasures = measureService.list();
        Map<String, Long> measureTypeCount = allMeasures.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getMeasureType() != null ? m.getMeasureType() : "其他",
                        Collectors.counting()));
        List<Map<String, Object>> measureDist = measureTypeCount.entrySet().stream()
                .map(e -> { Map<String, Object> m = new HashMap<>(); m.put("name", e.getKey()); m.put("value", e.getValue()); return m; })
                .collect(Collectors.toList());
        data.put("measureDist", measureDist);

        // 5. 需求完成率
        long totalNeeds = needsPublishService.count();
        long doneNeeds = needsPublishService.count(new LambdaQueryWrapper<NeedsPublish>().eq(NeedsPublish::getStatus, "2"));
        data.put("needRate", totalNeeds > 0 ? Math.round(doneNeeds * 100.0 / totalNeeds) : 0);

        // 6. 志愿者服务总时长
        Double totalHours = volunteerRecordService.getBaseMapper().selectObjs(
                new QueryWrapper<VolunteerRecord>().select("IFNULL(SUM(service_hours),0)")
        ).stream().mapToDouble(o -> Double.parseDouble(o.toString())).sum();
        data.put("totalVolunteerHours", Math.round(totalHours * 10) / 10.0);

        // 7. 就业申请统计
        long totalApps = jobApplicationService.count();
        long hiredApps = jobApplicationService.count(new LambdaQueryWrapper<com.poverty.modules.job.entity.JobApplication>().eq(com.poverty.modules.job.entity.JobApplication::getApplyStatus, 2));
        data.put("employmentRate", totalApps > 0 ? Math.round(hiredApps * 100.0 / totalApps) : 0);

        return R.ok(data);
    }
}
