package com.poverty.modules.job.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.job.entity.JobApplication;
import com.poverty.modules.job.mapper.JobApplicationMapper;
import com.poverty.modules.job.service.JobApplicationService;
import org.springframework.stereotype.Service;

@Service
public class JobApplicationServiceImpl extends ServiceImpl<JobApplicationMapper, JobApplication> implements JobApplicationService {
}
