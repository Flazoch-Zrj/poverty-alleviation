package com.poverty.modules.job.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poverty.common.result.R;
import com.poverty.common.result.PageResult;
import com.poverty.common.utils.SecurityUtils;
import com.poverty.modules.job.entity.JobApplication;
import com.poverty.modules.job.entity.JobPosition;
import com.poverty.modules.job.service.JobApplicationService;
import com.poverty.modules.job.service.JobPositionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "就业帮扶接口")
@RestController
@RequestMapping("/api/v1/job")
public class JobController {

    @Autowired
    private JobPositionService jobPositionService;

    @Autowired
    private JobApplicationService jobApplicationService;

    @ApiOperation("分页查询岗位列表")
    @GetMapping("/position/page")
    public R<PageResult<JobPosition>> getPositionPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<JobPosition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JobPosition::getIsValid, 1);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(JobPosition::getTitle, keyword);
        }
        wrapper.orderByDesc(JobPosition::getPublishTime);
        IPage<JobPosition> pageResult = jobPositionService.page(new Page<>(page, size), wrapper);
        return R.ok(PageResult.of(pageResult));
    }

    @ApiOperation("新增岗位")
    @PostMapping("/position")
    public R<Void> addPosition(@RequestBody JobPosition jobPosition) {
        if (jobPosition.getPublisherId() == null) {
            jobPosition.setPublisherId(SecurityUtils.getCurrentUserId());
        }
        jobPositionService.save(jobPosition);
        return R.ok();
    }

    @ApiOperation("修改岗位")
    @PutMapping("/position/{id}")
    public R<Void> updatePosition(@PathVariable Long id, @RequestBody JobPosition jobPosition) {
        jobPosition.setJobId(id);
        jobPositionService.updateById(jobPosition);
        return R.ok();
    }

    @ApiOperation("删除岗位（软删除）")
    @DeleteMapping("/position/{id}")
    public R<Void> deletePosition(@PathVariable Long id) {
        JobPosition jobPosition = new JobPosition();
        jobPosition.setJobId(id);
        jobPosition.setIsValid(0);
        jobPositionService.updateById(jobPosition);
        return R.ok();
    }

    @ApiOperation("新增应聘申请")
    @PostMapping("/application")
    public R<Void> addApplication(@RequestBody JobApplication jobApplication) {
        if (jobApplication.getApplicantUserId() == null) {
            jobApplication.setApplicantUserId(SecurityUtils.getCurrentUserId());
        }
        jobApplicationService.save(jobApplication);
        return R.ok();
    }

    @ApiOperation("分页查询应聘申请（含岗位名称）")
    @GetMapping("/application/page")
    public R<PageResult<JobApplication>> getApplicationPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long jobId,
            @RequestParam(required = false) Long applicantUserId) {
        LambdaQueryWrapper<JobApplication> wrapper = new LambdaQueryWrapper<>();
        if (jobId != null) {
            wrapper.eq(JobApplication::getJobId, jobId);
        }
        if (applicantUserId != null) {
            wrapper.eq(JobApplication::getApplicantUserId, applicantUserId);
        }
        wrapper.orderByDesc(JobApplication::getApplyTime);
        IPage<JobApplication> pageResult = jobApplicationService.page(new Page<>(page, size), wrapper);
        // 填充岗位名称
        for (JobApplication app : pageResult.getRecords()) {
            if (app.getJobId() != null) {
                JobPosition pos = jobPositionService.getById(app.getJobId());
                if (pos != null) {
                    app.setJobTitle(pos.getTitle());
                }
            }
        }
        return R.ok(PageResult.of(pageResult));
    }

    @ApiOperation("更新应聘申请状态")
    @PutMapping("/application/{id}/status")
    public R<Void> updateApplicationStatus(@PathVariable Long id, @RequestParam("status") Integer applyStatus) {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setApplyId(id);
        jobApplication.setApplyStatus(applyStatus);
        jobApplicationService.updateById(jobApplication);
        return R.ok();
    }
}
