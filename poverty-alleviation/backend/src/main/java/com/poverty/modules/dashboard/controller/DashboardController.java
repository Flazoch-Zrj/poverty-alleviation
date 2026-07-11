package com.poverty.modules.dashboard.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.poverty.common.result.R;
import com.poverty.modules.poverty.service.PovertyFamilyService;
import com.poverty.modules.assistance.service.AssistancePairingService;
import com.poverty.modules.visit.service.VisitRecordService;
import com.poverty.modules.donation.service.DonationMoneyService;
import com.poverty.modules.training.service.TrainingCourseService;
import com.poverty.modules.volunteer.service.VolunteerRecordService;
import com.poverty.modules.training.entity.TrainingCourse;
import com.poverty.modules.volunteer.entity.VolunteerRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "数据看板接口")
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private PovertyFamilyService povertyFamilyService;

    @Autowired
    private AssistancePairingService assistancePairingService;

    @Autowired
    private VisitRecordService visitRecordService;

    @Autowired
    private DonationMoneyService donationMoneyService;

    @Autowired
    private TrainingCourseService trainingCourseService;

    @Autowired
    private VolunteerRecordService volunteerRecordService;

    @ApiOperation("获取统计数据")
    @GetMapping("/statistics")
    public R<Map<String, Object>> statistics() {
        Map<String, Object> result = new HashMap<>();

        // total families
        long totalFamilies = povertyFamilyService.count();
        result.put("totalFamilies", totalFamilies);

        // paired families
        long pairedFamilies = assistancePairingService.count();
        result.put("pairedFamilies", pairedFamilies);

        // total visits
        long totalVisits = visitRecordService.count();
        result.put("totalVisits", totalVisits);

        // total donations (money amount sum)
        Double totalDonationAmount = donationMoneyService.getBaseMapper().selectObjs(
                new QueryWrapper<com.poverty.modules.donation.entity.DonationMoney>()
                        .select("IFNULL(SUM(amount), 0)")
                        .eq("status", 1)
        ).stream().mapToDouble(o -> Double.parseDouble(o.toString())).sum();
        result.put("totalDonationAmount", totalDonationAmount);

        // total donation count
        long totalDonations = donationMoneyService.count();
        result.put("totalDonations", totalDonations);

        // total training courses
        long totalTrainingCourses = trainingCourseService.count();
        result.put("totalTrainingCourses", totalTrainingCourses);

        // total volunteer hours (SUM of serviceHours where status >= 3)
        Double totalVolunteerHours = volunteerRecordService.getBaseMapper().selectObjs(
                new QueryWrapper<VolunteerRecord>()
                        .select("IFNULL(SUM(service_hours), 0)")
                        .ge("status", 3)
        ).stream().mapToDouble(o -> Double.parseDouble(o.toString())).sum();
        result.put("totalVolunteerHours", totalVolunteerHours);

        // completed activities count
        long totalVolunteerRecords = volunteerRecordService.count(
                new LambdaQueryWrapper<VolunteerRecord>().ge(VolunteerRecord::getStatus, 3));
        result.put("totalVolunteerRecords", totalVolunteerRecords);

        return R.ok(result);
    }
}
