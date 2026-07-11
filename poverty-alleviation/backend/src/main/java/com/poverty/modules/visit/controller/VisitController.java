package com.poverty.modules.visit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poverty.common.result.PageResult;
import com.poverty.common.result.R;
import com.poverty.modules.poverty.service.PovertyFamilyService;
import com.poverty.modules.visit.entity.VisitRecord;
import com.poverty.modules.visit.service.VisitRecordService;
import com.poverty.modules.volunteer.service.VolunteerScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api(tags = "走访记录接口")
@RestController
@RequestMapping("/api/v1/visit/record")
public class VisitController {

    @Autowired
    private VisitRecordService service;

    @Autowired
    private PovertyFamilyService povertyFamilyService;

    @Autowired
    private VolunteerScoreService volunteerScoreService;

    @GetMapping("/page")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @ApiOperation("分页查询走访记录")
    public R<PageResult<VisitRecord>> page(@RequestParam int page,
                                            @RequestParam int size,
                                            @RequestParam(required = false) Long familyId,
                                            @RequestParam(required = false) Long cadreUserId) {
        Page<VisitRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<VisitRecord> wrapper = new LambdaQueryWrapper<>();
        if (familyId != null) {
            wrapper.eq(VisitRecord::getFamilyId, familyId);
        }
        if (cadreUserId != null) {
            wrapper.eq(VisitRecord::getCadreUserId, cadreUserId);
        }
        wrapper.orderByDesc(VisitRecord::getCreateTime);
        IPage<VisitRecord> pageResult = service.page(pageParam, wrapper);
        // 填充户主姓名
        for (VisitRecord record : pageResult.getRecords()) {
            if (record.getFamilyId() != null) {
                com.poverty.modules.poverty.entity.PovertyFamily family = povertyFamilyService.getById(record.getFamilyId());
                if (family != null) {
                    record.setFamilyName(family.getHouseholderName());
                }
            }
        }
        return R.ok(PageResult.of(pageResult));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @ApiOperation("根据ID查询走访记录")
    public R<VisitRecord> getById(@PathVariable Long id) {
        return R.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @ApiOperation("新增走访记录")
    public R<String> save(@RequestBody VisitRecord entity) {
        service.save(entity);
        return R.ok("新增成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @ApiOperation("删除走访记录")
    public R<String> removeById(@PathVariable Long id) {
        service.removeById(id);
        return R.ok("删除成功");
    }

    @ApiOperation("志愿者提交走访")
    @PreAuthorize("hasRole('admin') or hasRole('cadre') or hasRole('volunteer')")
    @PostMapping("/volunteer")
    public R<?> volunteerSave(@RequestBody VisitRecord entity) {
        if (entity.getVolunteerUserId() == null) {
            entity.setVolunteerUserId(com.poverty.common.utils.SecurityUtils.getCurrentUserId());
        }
        service.save(entity);
        // 加积分
        volunteerScoreService.addScore(entity.getVolunteerUserId(), "referral", 15, entity.getRecordId(), "visit", "完成走访记录");
        return R.ok("走访提交成功，已加 15 积分");
    }

    @ApiOperation("志愿者查询我的走访记录")
    @PreAuthorize("hasRole('admin') or hasRole('cadre') or hasRole('volunteer')")
    @GetMapping("/volunteer/page")
    public R<PageResult<VisitRecord>> volunteerPage(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        Page<VisitRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<VisitRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VisitRecord::getVolunteerUserId, com.poverty.common.utils.SecurityUtils.getCurrentUserId());
        wrapper.orderByDesc(VisitRecord::getCreateTime);
        IPage<VisitRecord> pageResult = service.page(pageParam, wrapper);
        for (VisitRecord record : pageResult.getRecords()) {
            if (record.getFamilyId() != null) {
                com.poverty.modules.poverty.entity.PovertyFamily family = povertyFamilyService.getById(record.getFamilyId());
                if (family != null) record.setFamilyName(family.getHouseholderName());
            }
        }
        return R.ok(PageResult.of(pageResult));
    }
}
