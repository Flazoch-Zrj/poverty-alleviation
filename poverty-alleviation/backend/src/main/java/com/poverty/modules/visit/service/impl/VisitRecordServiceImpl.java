package com.poverty.modules.visit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.visit.entity.VisitRecord;
import com.poverty.modules.visit.mapper.VisitRecordMapper;
import com.poverty.modules.visit.service.VisitRecordService;
import org.springframework.stereotype.Service;

/**
 * 走访记录服务实现
 */
@Service
public class VisitRecordServiceImpl
        extends ServiceImpl<VisitRecordMapper, VisitRecord>
        implements VisitRecordService {
}
