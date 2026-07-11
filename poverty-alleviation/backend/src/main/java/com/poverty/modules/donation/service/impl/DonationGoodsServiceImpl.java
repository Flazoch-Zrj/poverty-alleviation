package com.poverty.modules.donation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.donation.entity.DonationGoods;
import com.poverty.modules.donation.mapper.DonationGoodsMapper;
import com.poverty.modules.donation.service.DonationGoodsService;
import org.springframework.stereotype.Service;

/**
 * 物资捐赠服务实现
 */
@Service
public class DonationGoodsServiceImpl extends ServiceImpl<DonationGoodsMapper, DonationGoods> implements DonationGoodsService {
}
