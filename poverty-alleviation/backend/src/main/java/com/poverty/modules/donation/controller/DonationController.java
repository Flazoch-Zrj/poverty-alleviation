package com.poverty.modules.donation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poverty.common.result.R;
import com.poverty.common.result.PageResult;
import com.poverty.common.utils.SecurityUtils;
import com.poverty.modules.donation.entity.DonationGoods;
import com.poverty.modules.donation.entity.DonationMoney;
import com.poverty.modules.donation.service.DonationGoodsService;
import com.poverty.modules.donation.service.DonationMoneyService;
import com.poverty.modules.volunteer.service.VolunteerScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api(tags = "捐赠管理接口")
@RestController
@RequestMapping("/api/v1/donation")
public class DonationController {

    @Autowired
    private DonationMoneyService donationMoneyService;

    @Autowired
    private DonationGoodsService donationGoodsService;

    // ==================== 捐款管理 ====================

    @GetMapping("/money/page")
    @ApiOperation("分页查询捐款列表")
    @PreAuthorize("hasRole('admin') or hasRole('volunteer')")
    public R<PageResult<DonationMoney>> moneyPage(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Page<DonationMoney> pageResult = donationMoneyService.page(new Page<>(page, size));
        return R.ok(PageResult.of(pageResult));
    }

    @PostMapping("/money")
    @ApiOperation("新增捐款")
    @PreAuthorize("hasRole('admin') or hasRole('volunteer')")
    public R<?> saveMoney(@RequestBody DonationMoney donationMoney) {
        // 自动设置登记人
        if (donationMoney.getRecorderId() == null) {
            donationMoney.setRecorderId(SecurityUtils.getCurrentUserId());
        }
        donationMoneyService.save(donationMoney);
        return R.ok("新增成功");
    }

    @PutMapping("/money/{id}")
    @ApiOperation("更新捐款")
    @PreAuthorize("hasRole('admin') or hasRole('volunteer')")
    public R<?> updateMoneyById(@PathVariable Long id, @RequestBody DonationMoney donationMoney) {
        donationMoney.setMoneyDonationId(id);
        donationMoneyService.updateById(donationMoney);
        return R.ok("更新成功");
    }

    @DeleteMapping("/money/{id}")
    @ApiOperation("删除捐款")
    @PreAuthorize("hasRole('admin') or hasRole('volunteer')")
    public R<?> removeMoneyById(@PathVariable Long id) {
        donationMoneyService.removeById(id);
        return R.ok("删除成功");
    }

    // ==================== 物资捐赠管理 ====================

    @GetMapping("/goods/page")
    @ApiOperation("分页查询物资捐赠列表")
    @PreAuthorize("hasRole('admin') or hasRole('volunteer')")
    public R<PageResult<DonationGoods>> goodsPage(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Page<DonationGoods> pageResult = donationGoodsService.page(new Page<>(page, size));
        return R.ok(PageResult.of(pageResult));
    }

    @PostMapping("/goods")
    @ApiOperation("新增物资捐赠")
    @PreAuthorize("hasRole('admin') or hasRole('volunteer')")
    public R<?> saveGoods(@RequestBody DonationGoods donationGoods) {
        // 自动设置登记人
        if (donationGoods.getRecorderId() == null) {
            donationGoods.setRecorderId(SecurityUtils.getCurrentUserId());
        }
        donationGoodsService.save(donationGoods);
        return R.ok("新增成功");
    }

    @PutMapping("/goods/{id}")
    @ApiOperation("更新物资捐赠")
    @PreAuthorize("hasRole('admin') or hasRole('volunteer')")
    public R<?> updateGoodsById(@PathVariable Long id, @RequestBody DonationGoods donationGoods) {
        donationGoods.setGoodsDonationId(id);
        donationGoodsService.updateById(donationGoods);
        return R.ok("更新成功");
    }

    @DeleteMapping("/goods/{id}")
    @ApiOperation("删除物资捐赠")
    @PreAuthorize("hasRole('admin') or hasRole('volunteer')")
    public R<?> removeGoodsById(@PathVariable Long id) {
        donationGoodsService.removeById(id);
        return R.ok("删除成功");
    }

    @Autowired
    private VolunteerScoreService volunteerScoreService;

    @ApiOperation("志愿者确认物资分发")
    @PreAuthorize("hasRole('admin') or hasRole('volunteer')")
    @PutMapping("/goods/{id}/distribute")
    public R<?> distributeGoods(@PathVariable Long id) {
        DonationGoods goods = donationGoodsService.getById(id);
        if (goods == null) return R.fail("物资不存在");
        goods.setDistributedBy(SecurityUtils.getCurrentUserId());
        goods.setStatus(3);
        donationGoodsService.updateById(goods);
        // 自动加积分
        volunteerScoreService.addScore(SecurityUtils.getCurrentUserId(), "referral", 30, id, "donation", "完成物资分发");
        return R.ok("分发确认成功");
    }
}
