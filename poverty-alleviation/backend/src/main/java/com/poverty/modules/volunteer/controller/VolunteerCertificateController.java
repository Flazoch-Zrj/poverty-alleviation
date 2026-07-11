package com.poverty.modules.volunteer.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poverty.common.result.R;
import com.poverty.common.result.PageResult;
import com.poverty.common.utils.SecurityUtils;
import com.poverty.modules.volunteer.entity.VolunteerCertificate;
import com.poverty.modules.volunteer.service.VolunteerCertificateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "志愿者证书接口")
@RestController
@RequestMapping("/api/v1/volunteer/certificate")
public class VolunteerCertificateController {

    @Autowired
    private VolunteerCertificateService volunteerCertificateService;

    @ApiOperation("分页查询我的证书")
    @GetMapping("/page")
    public R<PageResult<VolunteerCertificate>> getCertPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<VolunteerCertificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VolunteerCertificate::getUserId, userId);
        wrapper.orderByDesc(VolunteerCertificate::getIssueDate);
        IPage<VolunteerCertificate> pageResult = volunteerCertificateService.page(new Page<>(page, size), wrapper);
        return R.ok(PageResult.of(pageResult));
    }
}
