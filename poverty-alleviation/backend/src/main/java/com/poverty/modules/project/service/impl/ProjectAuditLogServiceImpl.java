package com.poverty.modules.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.project.entity.ProjectAuditLog;
import com.poverty.modules.project.mapper.ProjectAuditLogMapper;
import com.poverty.modules.project.service.ProjectAuditLogService;
import org.springframework.stereotype.Service;

/**
 * 项目审核日志服务实现
 */
@Service
public class ProjectAuditLogServiceImpl extends ServiceImpl<ProjectAuditLogMapper, ProjectAuditLog> implements ProjectAuditLogService {
}
