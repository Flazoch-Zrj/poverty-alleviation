package com.poverty.modules.job.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.job.entity.JobPosition;
import com.poverty.modules.job.mapper.JobPositionMapper;
import com.poverty.modules.job.service.JobPositionService;
import org.springframework.stereotype.Service;

@Service
public class JobPositionServiceImpl extends ServiceImpl<JobPositionMapper, JobPosition> implements JobPositionService {
}
