package com.poverty.security.service;

import com.poverty.modules.system.entity.SysUser;
import com.poverty.modules.system.service.SysUserService;
import com.poverty.security.entity.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户详情加载服务
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 查询用户权限列表
        List<String> permissions = new ArrayList<>();
        try {
            permissions = sysUserService.getUserPermissions(user.getUserId());
        } catch (Exception e) {
            // 如果权限表还没数据，给个默认权限
            permissions.add(user.getRoleCode() + ":access");
        }

        return new LoginUser(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                user.getRoleCode(),
                user.getStatus(),
                permissions
        );
    }
}
