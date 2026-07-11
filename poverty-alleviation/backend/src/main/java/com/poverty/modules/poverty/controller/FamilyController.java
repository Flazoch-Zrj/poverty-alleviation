package com.poverty.modules.poverty.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poverty.common.result.PageResult;
import com.poverty.common.result.R;
import com.poverty.modules.poverty.entity.FamilyMember;
import com.poverty.modules.poverty.entity.PovertyFamily;
import com.poverty.modules.poverty.service.FamilyMemberService;
import com.poverty.modules.poverty.service.PovertyFamilyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "贫困户档案接口")
@RestController("povertyFamilyController")
@RequestMapping("/api/v1/poverty/family")
public class FamilyController {

    @Autowired
    private PovertyFamilyService service;

    @Autowired
    private FamilyMemberService familyMemberService;

    @GetMapping("/page")
    @PreAuthorize("hasRole('admin') or hasRole('cadre') or hasRole('poor')")
    @ApiOperation("分页查询贫困户")
    public R<PageResult<PovertyFamily>> page(@RequestParam int page,
                                             @RequestParam int size,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) String status) {
        Page<PovertyFamily> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PovertyFamily> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(PovertyFamily::getHouseholderName, keyword)
                   .or()
                   .like(PovertyFamily::getFamilyCode, keyword);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(PovertyFamily::getStatus, status);
        }
        wrapper.orderByDesc(PovertyFamily::getCreateTime);
        IPage<PovertyFamily> pageResult = service.page(pageParam, wrapper);
        return R.ok(PageResult.of(pageResult));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('cadre') or hasRole('poor')")
    @ApiOperation("根据ID查询贫困户（含家庭成员）")
    public R<Map<String, Object>> getById(@PathVariable Long id) {
        PovertyFamily family = service.getById(id);
        if (family == null) {
            return R.fail("家庭不存在");
        }
        List<FamilyMember> members = familyMemberService.list(
                new LambdaQueryWrapper<FamilyMember>().eq(FamilyMember::getFamilyId, id));
        Map<String, Object> result = new HashMap<>();
        result.put("family", family);
        result.put("members", members);
        return R.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @ApiOperation("新增贫困户")
    public R<String> save(@RequestBody PovertyFamily entity) {
        service.save(entity);
        return R.ok("新增成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('cadre') or hasRole('poor')")
    @ApiOperation("更新贫困户")
    public R<String> updateById(@PathVariable Long id, @RequestBody PovertyFamily entity) {
        entity.setFamilyId(id);
        service.updateById(entity);
        return R.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @ApiOperation("删除贫困户")
    public R<String> removeById(@PathVariable Long id) {
        service.removeById(id);
        return R.ok("删除成功");
    }

    @GetMapping("/{familyId}/members")
    @PreAuthorize("hasRole('admin') or hasRole('cadre') or hasRole('poor')")
    @ApiOperation("获取家庭成员列表")
    public R<List<FamilyMember>> getFamilyMembers(@PathVariable Long familyId) {
        LambdaQueryWrapper<FamilyMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FamilyMember::getFamilyId, familyId);
        List<FamilyMember> list = familyMemberService.list(wrapper);
        return R.ok(list);
    }

    @PostMapping("/{familyId}/member")
    @PreAuthorize("hasRole('admin') or hasRole('cadre') or hasRole('poor')")
    @ApiOperation("新增家庭成员")
    public R<String> saveMember(@PathVariable Long familyId, @RequestBody FamilyMember entity) {
        entity.setFamilyId(familyId);
        familyMemberService.save(entity);
        return R.ok("新增成功");
    }

    @PutMapping("/member/{memberId}")
    @PreAuthorize("hasRole('admin') or hasRole('cadre') or hasRole('poor')")
    @ApiOperation("修改家庭成员")
    public R<String> updateMember(@PathVariable Long memberId, @RequestBody FamilyMember entity) {
        entity.setMemberId(memberId);
        familyMemberService.updateById(entity);
        return R.ok("修改成功");
    }

    @DeleteMapping("/member/{memberId}")
    @PreAuthorize("hasRole('admin') or hasRole('cadre') or hasRole('poor')")
    @ApiOperation("删除家庭成员")
    public R<String> removeMember(@PathVariable Long memberId) {
        familyMemberService.removeById(memberId);
        return R.ok("删除成功");
    }
}
