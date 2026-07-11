package com.poverty.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.poverty.modules.system.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT p.perm_code FROM sys_user_role ur " +
            "JOIN sys_role_permission rp ON ur.role_id = rp.role_id " +
            "JOIN sys_permission p ON rp.perm_id = p.perm_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> selectUserPermissions(@Param("userId") Long userId);
}
