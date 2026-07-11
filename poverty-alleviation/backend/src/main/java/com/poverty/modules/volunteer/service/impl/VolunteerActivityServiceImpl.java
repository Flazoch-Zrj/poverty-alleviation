package com.poverty.modules.volunteer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.volunteer.entity.VolunteerActivity;
import com.poverty.modules.volunteer.mapper.VolunteerActivityMapper;
import com.poverty.modules.volunteer.service.VolunteerActivityService;
import org.springframework.stereotype.Service;

@Service
public class VolunteerActivityServiceImpl extends ServiceImpl<VolunteerActivityMapper, VolunteerActivity> implements VolunteerActivityService {
}
