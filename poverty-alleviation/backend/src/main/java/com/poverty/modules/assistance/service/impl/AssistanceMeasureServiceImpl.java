package com.poverty.modules.assistance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.assistance.entity.AssistanceMeasure;
import com.poverty.modules.assistance.mapper.AssistanceMeasureMapper;
import com.poverty.modules.assistance.service.AssistanceMeasureService;
import org.springframework.stereotype.Service;

/**
 * 帮扶措施服务实现
 */
@Service
public class AssistanceMeasureServiceImpl
        extends ServiceImpl<AssistanceMeasureMapper, AssistanceMeasure>
        implements AssistanceMeasureService {
}
