package com.poverty.modules.poverty.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.poverty.entity.FamilyMember;
import com.poverty.modules.poverty.mapper.FamilyMemberMapper;
import com.poverty.modules.poverty.service.FamilyMemberService;
import org.springframework.stereotype.Service;

@Service
public class FamilyMemberServiceImpl extends ServiceImpl<FamilyMemberMapper, FamilyMember> implements FamilyMemberService {
}
