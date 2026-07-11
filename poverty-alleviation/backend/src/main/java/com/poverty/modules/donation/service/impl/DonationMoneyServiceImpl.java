package com.poverty.modules.donation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.donation.entity.DonationMoney;
import com.poverty.modules.donation.mapper.DonationMoneyMapper;
import com.poverty.modules.donation.service.DonationMoneyService;
import com.poverty.modules.need.entity.NeedsPublish;
import com.poverty.modules.need.service.NeedsPublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DonationMoneyServiceImpl extends ServiceImpl<DonationMoneyMapper, DonationMoney> implements DonationMoneyService {

    @Autowired
    private NeedsPublishService needsPublishService;

    @Override
    public boolean save(DonationMoney entity) {
        boolean result = super.save(entity);
        if (result && entity.getStatus() == 1 && entity.getNeedId() != null) {
            updateNeedActualAmount(entity.getNeedId());
        }
        return result;
    }

    @Override
    public boolean updateById(DonationMoney entity) {
        DonationMoney old = getById(entity.getMoneyDonationId());
        boolean result = super.updateById(entity);
        if (result && entity.getNeedId() != null) {
            updateNeedActualAmount(entity.getNeedId());
        } else if (result && old != null && old.getNeedId() != null) {
            updateNeedActualAmount(old.getNeedId());
        }
        return result;
    }

    private void updateNeedActualAmount(Long needId) {
        LambdaQueryWrapper<DonationMoney> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DonationMoney::getNeedId, needId);
        wrapper.eq(DonationMoney::getStatus, 1);
        BigDecimal total = list(wrapper).stream()
                .map(d -> d.getAmount() != null ? d.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        NeedsPublish need = needsPublishService.getById(needId);
        if (need != null) {
            need.setActualAmount(total);
            // 如果已达标，自动标记为已完成
            if (need.getTargetAmount() != null && total.compareTo(need.getTargetAmount()) >= 0) {
                need.setStatus("2");
            }
            needsPublishService.updateById(need);
        }
    }
}
