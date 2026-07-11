package com.poverty.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.system.entity.SysUser;
import com.poverty.modules.system.mapper.SysUserMapper;
import com.poverty.modules.system.service.SysUserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .last("LIMIT 1"));
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        return baseMapper.selectUserPermissions(userId);
    }
}
