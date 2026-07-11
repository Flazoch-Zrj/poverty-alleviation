package com.poverty.modules.assistance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.assistance.entity.AssistancePairing;
import com.poverty.modules.assistance.mapper.AssistancePairingMapper;
import com.poverty.modules.assistance.service.AssistancePairingService;
import org.springframework.stereotype.Service;

/**
 * 帮扶结对服务实现
 */
@Service
public class AssistancePairingServiceImpl
        extends ServiceImpl<AssistancePairingMapper, AssistancePairing>
        implements AssistancePairingService {
}
