package com.poverty.modules.need.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poverty.common.result.PageResult;
import com.poverty.common.result.R;
import com.poverty.common.utils.SecurityUtils;
import com.poverty.modules.need.entity.NeedsPublish;
import com.poverty.modules.need.service.NeedsPublishService;
import com.poverty.modules.poverty.service.PovertyFamilyService;
import com.poverty.modules.volunteer.service.VolunteerScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api(tags = "困难需求接口")
@RestController
@RequestMapping("/api/v1/need")
public class NeedController {

    @Autowired
    private NeedsPublishService needsPublishService;

    @Autowired
    private VolunteerScoreService volunteerScoreService;

    @Autowired
    private com.poverty.modules.poverty.service.PovertyFamilyService povertyFamilyService;

    @GetMapping("/page")
    @ApiOperation("分页查询需求列表")
    public R<PageResult<NeedsPublish>> page(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) String status) {
        LambdaQueryWrapper<NeedsPublish> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(NeedsPublish::getStatus, status);
        }
        wrapper.orderByDesc(NeedsPublish::getCreateTime);
        IPage<NeedsPublish> pageResult = needsPublishService.page(new Page<>(page, size), wrapper);
        return R.ok(PageResult.of(pageResult));
    }

    @PostMapping
    @ApiOperation("新增需求")
    @PreAuthorize("hasRole('admin') or hasRole('cadre') or hasRole('poor')")
    public R<?> save(@RequestBody NeedsPublish needsPublish) {
        if (needsPublish.getPublisherId() == null) {
            needsPublish.setPublisherId(SecurityUtils.getCurrentUserId());
        }
        if (needsPublish.getFamilyId() == null) {
            // 自动查找该用户关联的家庭
            Long userId = needsPublish.getPublisherId();
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.poverty.modules.poverty.entity.PovertyFamily> fw = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            fw.eq(com.poverty.modules.poverty.entity.PovertyFamily::getHouseholderId, userId).last("LIMIT 1");
            com.poverty.modules.poverty.entity.PovertyFamily family = povertyFamilyService.getOne(fw);
            if (family != null) {
                needsPublish.setFamilyId(family.getFamilyId());
            }
        }
        needsPublishService.save(needsPublish);
        return R.ok("新增成功");
    }

    @PutMapping("/{id}")
    @ApiOperation("更新需求")
    public R<?> updateById(@PathVariable Long id, @RequestBody NeedsPublish needsPublish) {
        needsPublish.setNeedId(id);
        needsPublishService.updateById(needsPublish);
        return R.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除需求")
    public R<?> removeById(@PathVariable Long id) {
        needsPublishService.removeById(id);
        return R.ok("删除成功");
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询需求")
    public R<NeedsPublish> getById(@PathVariable Long id) {
        NeedsPublish needsPublish = needsPublishService.getById(id);
        return R.ok(needsPublish);
    }

    @ApiOperation("志愿者认领需求")
    @PreAuthorize("hasRole('volunteer') or hasRole('admin')")
    @PutMapping("/{id}/claim")
    public R<?> claim(@PathVariable Long id) {
        NeedsPublish need = needsPublishService.getById(id);
        if (need == null) {
            return R.fail("需求不存在");
        }
        if (!"0".equals(need.getStatus())) {
            return R.fail("该需求已被认领或已完成");
        }
        need.setVolunteerId(SecurityUtils.getCurrentUserId());
        need.setVolunteerConfirmTime(java.time.LocalDateTime.now());
        need.setStatus("1");
        needsPublishService.updateById(need);
        return R.ok("认领成功");
    }

    @ApiOperation("完成需求（自动给认领志愿者加分）")
    @PreAuthorize("hasRole('volunteer') or hasRole('admin')")
    @PutMapping("/{id}/complete")
    public R<?> complete(@PathVariable Long id) {
        NeedsPublish need = needsPublishService.getById(id);
        if (need == null) {
            return R.fail("需求不存在");
        }
        need.setStatus("2");
        needsPublishService.updateById(need);
        // 给认领的志愿者加分
        if (need.getVolunteerId() != null) {
            volunteerScoreService.addScore(
                need.getVolunteerId(), "referral", 50,
                id, "need", "完成困难需求认领");
        }
        return R.ok("需求已标记为完成");
    }

    @ApiOperation("审核需求（更新状态）")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @PutMapping("/{id}/review")
    public R<?> review(@PathVariable Long id, @RequestParam String status) {
        NeedsPublish need = needsPublishService.getById(id);
        if (need == null) {
            return R.fail("需求不存在");
        }
        need.setStatus(status);
        needsPublishService.updateById(need);
        return R.ok("状态已更新");
    }
}
