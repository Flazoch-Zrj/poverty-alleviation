package com.poverty.modules.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.project.entity.PovertyProject;
import com.poverty.modules.project.mapper.PovertyProjectMapper;
import com.poverty.modules.project.service.PovertyProjectService;
import org.springframework.stereotype.Service;

/**
 * 扶贫项目服务实现
 */
@Service
public class PovertyProjectServiceImpl extends ServiceImpl<PovertyProjectMapper, PovertyProject> implements PovertyProjectService {
}
