package com.poverty.modules.volunteer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.volunteer.entity.VolunteerRecord;
import com.poverty.modules.volunteer.mapper.VolunteerRecordMapper;
import com.poverty.modules.volunteer.service.VolunteerRecordService;
import org.springframework.stereotype.Service;

@Service
public class VolunteerRecordServiceImpl extends ServiceImpl<VolunteerRecordMapper, VolunteerRecord> implements VolunteerRecordService {
}
