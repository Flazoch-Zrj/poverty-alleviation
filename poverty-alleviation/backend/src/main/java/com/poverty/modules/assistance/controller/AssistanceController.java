package com.poverty.modules.assistance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poverty.common.result.PageResult;
import com.poverty.common.result.R;
import com.poverty.modules.assistance.entity.AssistanceMeasure;
import com.poverty.modules.assistance.entity.AssistancePairing;
import com.poverty.modules.assistance.service.AssistanceMeasureService;
import com.poverty.modules.assistance.service.AssistancePairingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api(tags = "帮扶结对接口")
@RestController
@RequestMapping("/api/v1/assistance")
public class AssistanceController {

    @Autowired
    private AssistancePairingService pairingService;

    @Autowired
    private AssistanceMeasureService measureService;

    // ==================== 帮扶结对 ====================

    @GetMapping("/pairing/page")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @ApiOperation("分页查询帮扶结对")
    public R<PageResult<AssistancePairing>> pairingPage(@RequestParam int page,
                                                         @RequestParam int size,
                                                         @RequestParam(required = false) String keyword) {
        Page<AssistancePairing> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AssistancePairing> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(AssistancePairing::getRemark, keyword);
        }
        IPage<AssistancePairing> pageResult = pairingService.page(pageParam, wrapper);
        return R.ok(PageResult.of(pageResult));
    }

    @GetMapping("/pairing/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @ApiOperation("根据ID查询帮扶结对")
    public R<AssistancePairing> pairingGetById(@PathVariable Long id) {
        return R.ok(pairingService.getById(id));
    }

    @PostMapping("/pairing")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @ApiOperation("新增帮扶结对")
    public R<String> pairingSave(@RequestBody AssistancePairing entity) {
        pairingService.save(entity);
        return R.ok("新增成功");
    }

    @PutMapping("/pairing/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @ApiOperation("更新帮扶结对")
    public R<String> pairingUpdateById(@PathVariable Long id, @RequestBody AssistancePairing entity) {
        entity.setPairingId(id);
        pairingService.updateById(entity);
        return R.ok("更新成功");
    }

    @DeleteMapping("/pairing/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @ApiOperation("删除帮扶结对")
    public R<String> pairingRemoveById(@PathVariable Long id) {
        pairingService.removeById(id);
        return R.ok("删除成功");
    }

    // ==================== 帮扶措施 ====================

    @GetMapping("/measure/page")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @ApiOperation("分页查询帮扶措施")
    public R<PageResult<AssistanceMeasure>> measurePage(@RequestParam int page,
                                                         @RequestParam int size,
                                                         @RequestParam(required = false) Long familyId) {
        Page<AssistanceMeasure> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AssistanceMeasure> wrapper = new LambdaQueryWrapper<>();
        if (familyId != null) {
            wrapper.eq(AssistanceMeasure::getFamilyId, familyId);
        }
        IPage<AssistanceMeasure> pageResult = measureService.page(pageParam, wrapper);
        return R.ok(PageResult.of(pageResult));
    }

    @PostMapping("/measure")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @ApiOperation("新增帮扶措施")
    public R<String> measureSave(@RequestBody AssistanceMeasure entity) {
        measureService.save(entity);
        return R.ok("新增成功");
    }

    @PutMapping("/measure/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @ApiOperation("更新帮扶措施")
    public R<String> measureUpdateById(@PathVariable Long id, @RequestBody AssistanceMeasure entity) {
        entity.setMeasureId(id);
        measureService.updateById(entity);
        return R.ok("更新成功");
    }

    @DeleteMapping("/measure/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    @ApiOperation("删除帮扶措施")
    public R<String> measureRemoveById(@PathVariable Long id) {
        measureService.removeById(id);
        return R.ok("删除成功");
    }
}
