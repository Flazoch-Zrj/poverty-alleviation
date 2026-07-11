package com.poverty.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poverty.common.result.PageResult;
import com.poverty.common.result.R;
import com.poverty.modules.system.entity.SysRole;
import com.poverty.modules.system.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "角色管理接口")
@RestController("systemRoleController")
@RequestMapping("/api/v1/system/role")
public class RoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/page")
    @ApiOperation("分页查询角色列表")
    @PreAuthorize("hasRole('admin')")
    public R<PageResult<SysRole>> page(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Page<SysRole> result = sysRoleService.page(new Page<>(page, size));
        return R.ok(PageResult.of(result));
    }

    @GetMapping("/list")
    @ApiOperation("查询所有角色")
    public R<List<SysRole>> list() {
        return R.ok(sysRoleService.list());
    }

    @GetMapping("/{id}")
    @ApiOperation("根据 ID 获取角色")
    @PreAuthorize("hasRole('admin')")
    public R<SysRole> getById(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        if (role == null) return R.fail("角色不存在");
        return R.ok(role);
    }

    @PostMapping
    @ApiOperation("新增角色")
    @PreAuthorize("hasRole('admin')")
    public R<?> create(@RequestBody SysRole role) {
        sysRoleService.save(role);
        return R.ok("新增成功");
    }

    @PutMapping("/{id}")
    @ApiOperation("更新角色")
    @PreAuthorize("hasRole('admin')")
    public R<?> update(@PathVariable Long id, @RequestBody SysRole role) {
        role.setRoleId(id);
        sysRoleService.updateById(role);
        return R.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除角色")
    @PreAuthorize("hasRole('admin')")
    public R<?> delete(@PathVariable Long id) {
        sysRoleService.removeById(id);
        return R.ok("删除成功");
    }
}
