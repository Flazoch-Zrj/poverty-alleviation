package com.poverty.modules.training.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.training.entity.TrainingEnrollment;
import com.poverty.modules.training.mapper.TrainingEnrollmentMapper;
import com.poverty.modules.training.service.TrainingEnrollmentService;
import org.springframework.stereotype.Service;

@Service
public class TrainingEnrollmentServiceImpl extends ServiceImpl<TrainingEnrollmentMapper, TrainingEnrollment> implements TrainingEnrollmentService {
}
