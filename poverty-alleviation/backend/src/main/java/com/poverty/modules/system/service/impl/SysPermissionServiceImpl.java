package com.poverty.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.system.entity.SysPermission;
import com.poverty.modules.system.mapper.SysPermissionMapper;
import com.poverty.modules.system.service.SysPermissionService;
import org.springframework.stereotype.Service;

@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {
}
