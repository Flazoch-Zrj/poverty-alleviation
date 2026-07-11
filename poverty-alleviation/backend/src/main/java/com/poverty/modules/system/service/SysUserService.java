package com.poverty.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.poverty.modules.system.entity.SysUser;

import java.util.List;

public interface SysUserService extends IService<SysUser> {

    SysUser getByUsername(String username);

    List<String> getUserPermissions(Long userId);
}
