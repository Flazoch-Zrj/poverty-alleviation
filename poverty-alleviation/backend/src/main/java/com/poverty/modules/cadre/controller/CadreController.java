package com.poverty.modules.cadre.controller;

import com.poverty.common.result.R;
import com.poverty.common.utils.SecurityUtils;
import com.poverty.modules.cadre.service.CadreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "干部工作台接口")
@RestController
@RequestMapping("/api/v1/cadre")
@PreAuthorize("hasRole('admin') or hasRole('cadre')")
public class CadreController {

    @Autowired
    private CadreService cadreService;

    @ApiOperation("干部工作台数据")
    @GetMapping("/dashboard")
    public R<Map<String, Object>> dashboard() {
        return R.ok(cadreService.getDashboard(SecurityUtils.getCurrentUserId()));
    }

    @ApiOperation("我的帮扶家庭列表")
    @GetMapping("/families")
    public R<List<Map<String, Object>>> getMyFamilies() {
        Map<String, Object> dash = cadreService.getDashboard(SecurityUtils.getCurrentUserId());
        List<Map<String, Object>> families = (List<Map<String, Object>>) dash.get("families");
        return R.ok(families);
    }

    @ApiOperation("家庭全息视图")
    @GetMapping("/family/{familyId}/overview")
    public R<Map<String, Object>> familyOverview(@PathVariable Long familyId) {
        return R.ok(cadreService.getFamilyOverview(familyId));
    }

    @ApiOperation("家庭帮扶时间线")
    @GetMapping("/family/{familyId}/timeline")
    public R<List<Map<String, Object>>> familyTimeline(@PathVariable Long familyId) {
        return R.ok(cadreService.getFamilyTimeline(familyId));
    }

    @ApiOperation("我的待办事项")
    @GetMapping("/tasks")
    public R<List<Map<String, Object>>> getTasks() {
        Map<String, Object> dash = cadreService.getDashboard(SecurityUtils.getCurrentUserId());
        return R.ok((List<Map<String, Object>>) dash.get("tasks"));
    }
}
