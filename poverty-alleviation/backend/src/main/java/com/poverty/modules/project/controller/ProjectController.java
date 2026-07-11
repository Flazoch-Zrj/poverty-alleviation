package com.poverty.modules.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poverty.common.result.PageResult;
import com.poverty.common.result.R;
import com.poverty.common.utils.SecurityUtils;
import com.poverty.modules.project.entity.PovertyProject;
import com.poverty.modules.project.entity.ProjectAuditLog;
import com.poverty.modules.project.service.PovertyProjectService;
import com.poverty.modules.project.service.ProjectAuditLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Api(tags = "项目管理接口")
@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {

    @Autowired
    private PovertyProjectService povertyProjectService;

    @Autowired
    private ProjectAuditLogService projectAuditLogService;

    @GetMapping("/page")
    @ApiOperation("分页查询项目列表")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    public R<PageResult<PovertyProject>> page(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) String status) {
        LambdaQueryWrapper<PovertyProject> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(PovertyProject::getProjectName, keyword);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(PovertyProject::getStatus, status);
        }
        wrapper.orderByDesc(PovertyProject::getCreateTime);
        IPage<PovertyProject> pageResult = povertyProjectService.page(new Page<>(page, size), wrapper);
        return R.ok(PageResult.of(pageResult));
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询项目")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    public R<PovertyProject> getById(@PathVariable Long id) {
        PovertyProject project = povertyProjectService.getById(id);
        return R.ok(project);
    }

    @PostMapping
    @ApiOperation("新增项目")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    public R<?> save(@RequestBody PovertyProject povertyProject) {
        povertyProjectService.save(povertyProject);
        return R.ok("新增成功");
    }

    @PutMapping("/{id}")
    @ApiOperation("更新项目")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    public R<?> updateById(@PathVariable Long id, @RequestBody PovertyProject povertyProject) {
        povertyProject.setProjectId(id);
        povertyProjectService.updateById(povertyProject);
        return R.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除项目")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    public R<?> removeById(@PathVariable Long id) {
        povertyProjectService.removeById(id);
        return R.ok("删除成功");
    }

    @PostMapping("/{id}/audit")
    @ApiOperation("审核项目")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    public R<?> audit(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String action = (String) body.get("action");
        String comment = (String) body.get("comment");

        ProjectAuditLog auditLog = new ProjectAuditLog();
        auditLog.setProjectId(id);
        auditLog.setAuditorId(SecurityUtils.getCurrentUserId());
        auditLog.setAction(action);
        auditLog.setComment(comment);
        auditLog.setCreateTime(LocalDateTime.now());
        projectAuditLogService.save(auditLog);

        PovertyProject project = povertyProjectService.getById(id);
        if ("通过".equals(action)) {
            project.setStatus("1");
        } else if ("驳回".equals(action)) {
            project.setStatus("2");
        }
        povertyProjectService.updateById(project);

        return R.ok("审核成功");
    }

    @GetMapping("/{id}/audit-logs")
    @ApiOperation("查看项目审核日志")
    @PreAuthorize("hasRole('admin') or hasRole('cadre')")
    public R<?> auditLogs(@PathVariable Long id) {
        LambdaQueryWrapper<ProjectAuditLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectAuditLog::getProjectId, id);
        wrapper.orderByDesc(ProjectAuditLog::getCreateTime);
        return R.ok(projectAuditLogService.list(wrapper));
    }
}
