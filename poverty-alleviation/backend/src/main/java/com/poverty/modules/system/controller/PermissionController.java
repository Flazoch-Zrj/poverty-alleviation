package com.poverty.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poverty.common.result.PageResult;
import com.poverty.common.result.R;
import com.poverty.modules.system.entity.SysPermission;
import com.poverty.modules.system.service.SysPermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "权限管理接口")
@RestController("systemPermissionController")
@RequestMapping("/api/v1/system/permission")
public class PermissionController {

    @Autowired
    private SysPermissionService sysPermissionService;

    @GetMapping("/list")
    @ApiOperation("查询所有权限")
    @PreAuthorize("hasRole('admin')")
    public R<List<SysPermission>> list() {
        return R.ok(sysPermissionService.list());
    }

    @GetMapping("/tree")
    @ApiOperation("查询权限树")
    @PreAuthorize("hasRole('admin')")
    public R<List<SysPermission>> tree() {
        List<SysPermission> all = sysPermissionService.list();
        return R.ok(all);
    }

    @PostMapping
    @ApiOperation("新增权限")
    @PreAuthorize("hasRole('admin')")
    public R<?> create(@RequestBody SysPermission permission) {
        sysPermissionService.save(permission);
        return R.ok("新增成功");
    }

    @PutMapping("/{id}")
    @ApiOperation("更新权限")
    @PreAuthorize("hasRole('admin')")
    public R<?> update(@PathVariable Long id, @RequestBody SysPermission permission) {
        permission.setPermId(id);
        sysPermissionService.updateById(permission);
        return R.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除权限")
    @PreAuthorize("hasRole('admin')")
    public R<?> delete(@PathVariable Long id) {
        sysPermissionService.removeById(id);
        return R.ok("删除成功");
    }
}
