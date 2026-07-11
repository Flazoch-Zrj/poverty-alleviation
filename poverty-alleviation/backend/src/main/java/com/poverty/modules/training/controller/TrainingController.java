package com.poverty.modules.training.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poverty.common.result.R;
import com.poverty.common.result.PageResult;
import com.poverty.common.utils.SecurityUtils;
import com.poverty.modules.training.entity.TrainingCourse;
import com.poverty.modules.training.entity.TrainingEnrollment;
import com.poverty.modules.training.service.TrainingCourseService;
import com.poverty.modules.training.service.TrainingEnrollmentService;
import com.poverty.modules.volunteer.service.VolunteerScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Api(tags = "技能培训接口")
@RestController
@RequestMapping("/api/v1/training")
public class TrainingController {

    @Autowired
    private TrainingCourseService trainingCourseService;

    @Autowired
    private TrainingEnrollmentService trainingEnrollmentService;

    @Autowired
    private VolunteerScoreService volunteerScoreService;

    @ApiOperation("分页查询课程列表")
    @PreAuthorize("hasRole('admin') or hasRole('cadre') or hasRole('poor') or hasRole('volunteer')")
    @GetMapping("/course/page")
    public R<PageResult<TrainingCourse>> getCoursePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<TrainingCourse> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(TrainingCourse::getStatus, status);
        }
        IPage<TrainingCourse> pageResult = trainingCourseService.page(new Page<>(page, size), wrapper);
        return R.ok(PageResult.of(pageResult));
    }

    @ApiOperation("新增课程")
    @PreAuthorize("hasRole('admin') or hasRole('cadre') or hasRole('volunteer')")
    @PostMapping("/course")
    public R<Void> addCourse(@RequestBody TrainingCourse trainingCourse) {
        // 自动设置发布人
        if (trainingCourse.getPublisherId() == null) {
            trainingCourse.setPublisherId(SecurityUtils.getCurrentUserId());
        }
        trainingCourseService.save(trainingCourse);
        return R.ok();
    }

    @ApiOperation("修改课程")
    @PreAuthorize("hasRole('admin') or hasRole('cadre') or hasRole('volunteer')")
    @PutMapping("/course/{id}")
    public R<Void> updateCourse(@PathVariable Long id, @RequestBody TrainingCourse trainingCourse) {
        trainingCourse.setCourseId(id);
        trainingCourseService.updateById(trainingCourse);
        return R.ok();
    }

    @ApiOperation("删除课程")
    @PreAuthorize("hasRole('admin') or hasRole('cadre') or hasRole('volunteer')")
    @DeleteMapping("/course/{id}")
    public R<Void> deleteCourse(@PathVariable Long id) {
        trainingCourseService.removeById(id);
        return R.ok();
    }

    @ApiOperation("新增报名（需校验名额 + 防重复）")
    @PostMapping("/enrollment")
    public R<Void> addEnrollment(@RequestBody TrainingEnrollment trainingEnrollment) {
        if (trainingEnrollment.getUserId() == null) {
            trainingEnrollment.setUserId(SecurityUtils.getCurrentUserId());
        }
        // 检查是否已报名
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TrainingEnrollment> chk = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        chk.eq(TrainingEnrollment::getCourseId, trainingEnrollment.getCourseId());
        chk.eq(TrainingEnrollment::getUserId, trainingEnrollment.getUserId());
        if (trainingEnrollmentService.count(chk) > 0) {
            return R.fail("您已报名该课程，请勿重复报名");
        }
        TrainingCourse course = trainingCourseService.getById(trainingEnrollment.getCourseId());
        if (course != null && course.getEnrolledCount() < course.getMaxEnroll()) {
            trainingEnrollment.setStatus(0); // 0=待审核
            trainingEnrollmentService.save(trainingEnrollment);
        }
        return R.ok();
    }

    @ApiOperation("分页查询报名记录")
    @GetMapping("/enrollment/page")
    public R<PageResult<TrainingEnrollment>> getEnrollmentPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long courseId) {
        LambdaQueryWrapper<TrainingEnrollment> wrapper = new LambdaQueryWrapper<>();
        if (courseId != null) {
            wrapper.eq(TrainingEnrollment::getCourseId, courseId);
        }
        IPage<TrainingEnrollment> pageResult = trainingEnrollmentService.page(new Page<>(page, size), wrapper);
        // 填充课程名称
        for (TrainingEnrollment enroll : pageResult.getRecords()) {
            if (enroll.getCourseId() != null) {
                TrainingCourse course = trainingCourseService.getById(enroll.getCourseId());
                if (course != null) {
                    enroll.setCourseTitle(course.getTitle());
                }
            }
        }
        return R.ok(PageResult.of(pageResult));
    }

    @ApiOperation("审核报名（admin/cadre）")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @PutMapping("/enrollment/{id}/review")
    public R<Void> reviewEnrollment(@PathVariable Long id, @RequestParam Integer status) {
        TrainingEnrollment enrollment = new TrainingEnrollment();
        enrollment.setEnrollId(id);
        enrollment.setStatus(status); // 1=通过, 3=拒绝
        trainingEnrollmentService.updateById(enrollment);
        // 通过时增加课程报名数
        if (status == 1) {
            TrainingEnrollment full = trainingEnrollmentService.getById(id);
            if (full != null && full.getCourseId() != null) {
                TrainingCourse course = trainingCourseService.getById(full.getCourseId());
                if (course != null) {
                    course.setEnrolledCount(course.getEnrolledCount() + 1);
                    trainingCourseService.updateById(course);
                }
            }
        }
        return R.ok();
    }

    @ApiOperation("签到（自动加积分）")
    @PutMapping("/enrollment/{id}/sign-in")
    public R<Void> signIn(@PathVariable Long id) {
        TrainingEnrollment existing = trainingEnrollmentService.getById(id);
        if (existing == null || existing.getStatus() != 1) {
            return R.fail("仅审核通过的报名才能签到");
        }
        TrainingEnrollment trainingEnrollment = new TrainingEnrollment();
        trainingEnrollment.setEnrollId(id);
        trainingEnrollment.setSignInTime(LocalDateTime.now());
        trainingEnrollment.setStatus(2);
        trainingEnrollmentService.updateById(trainingEnrollment);
        // 培训签到自动加积分
        TrainingEnrollment enrollment = trainingEnrollmentService.getById(id);
        if (enrollment != null && enrollment.getUserId() != null) {
            volunteerScoreService.addScore(
                enrollment.getUserId(), "training", 20,
                id, "training", "完成培训签到");
        }
        return R.ok();
    }
}
