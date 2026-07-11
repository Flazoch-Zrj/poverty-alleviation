package com.poverty.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poverty.common.result.PageResult;
import com.poverty.common.result.R;
import com.poverty.common.utils.SecurityUtils;
import com.poverty.modules.system.entity.SysUser;
import com.poverty.modules.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户管理接口")
@RestController("systemUserController")
@RequestMapping("/api/v1/system/user")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/page")
    @ApiOperation("分页查询用户列表")
    @PreAuthorize("hasRole('admin')")
    public R<PageResult<SysUser>> page(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(required = false) String keyword) {
        Page<SysUser> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getRealName, keyword);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = sysUserService.page(pageParam, wrapper);
        return R.ok(PageResult.of(result));
    }

    @GetMapping("/{id}")
    @ApiOperation("根据 ID 获取用户详情")
    @PreAuthorize("hasRole('admin')")
    public R<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            return R.fail("用户不存在");
        }
        user.setPassword(null); // 脱敏
        return R.ok(user);
    }

    @PostMapping
    @ApiOperation("新增用户")
    @PreAuthorize("hasRole('admin')")
    public R<?> create(@RequestBody SysUser user) {
        user.setUserId(null); // 强制使用数据库自增，防止前端误传 userId
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        sysUserService.save(user);
        return R.ok("新增成功");
    }

    @PutMapping("/{id}")
    @ApiOperation("更新用户")
    @PreAuthorize("hasRole('admin')")
    public R<?> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setUserId(id);
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        sysUserService.updateById(user);
        return R.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    @PreAuthorize("hasRole('admin')")
    public R<?> delete(@PathVariable Long id) {
        try {
            // 不能删除自己
            Long currentUserId = SecurityUtils.getCurrentUserId();
            if (currentUserId != null && currentUserId.equals(id)) {
                return R.fail("不能删除当前登录用户");
            }
            sysUserService.removeById(id);
            return R.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("删除失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    @ApiOperation("启停用户")
    @PreAuthorize("hasRole('admin')")
    public R<?> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setUserId(id);
        user.setStatus(status);
        sysUserService.updateById(user);
        return R.ok("操作成功");
    }

    @PutMapping("/{id}/reset-password")
    @ApiOperation("重置密码")
    @PreAuthorize("hasRole('admin')")
    public R<?> resetPassword(@PathVariable Long id) {
        SysUser user = new SysUser();
        user.setUserId(id);
        user.setPassword(passwordEncoder.encode("123456"));
        sysUserService.updateById(user);
        return R.ok("密码已重置为 123456");
    }
}
