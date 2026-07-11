package com.poverty.modules.poverty.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.poverty.entity.PovertyFamily;
import com.poverty.modules.poverty.mapper.PovertyFamilyMapper;
import com.poverty.modules.poverty.service.PovertyFamilyService;
import org.springframework.stereotype.Service;

@Service
public class PovertyFamilyServiceImpl extends ServiceImpl<PovertyFamilyMapper, PovertyFamily> implements PovertyFamilyService {
}
