package com.poverty.modules.training.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.training.entity.TrainingCourse;
import com.poverty.modules.training.mapper.TrainingCourseMapper;
import com.poverty.modules.training.service.TrainingCourseService;
import org.springframework.stereotype.Service;

@Service
public class TrainingCourseServiceImpl extends ServiceImpl<TrainingCourseMapper, TrainingCourse> implements TrainingCourseService {
}
