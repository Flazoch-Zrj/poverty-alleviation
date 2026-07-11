package com.poverty.modules.risk.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poverty.common.result.R;
import com.poverty.common.result.PageResult;
import com.poverty.modules.risk.entity.RiskAlert;
import com.poverty.modules.risk.entity.RiskAssessment;
import com.poverty.modules.risk.service.RiskService;
import com.poverty.modules.risk.entity.SmartMatch;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "防返贫预警接口")
@RestController
@RequestMapping("/api/v1/risk")
@PreAuthorize("hasRole('admin') or hasRole('cadre')")
public class RiskController {

    @Autowired private RiskService riskService;
    @Autowired private com.poverty.modules.risk.mapper.RiskAssessmentMapper riskAssessmentMapper;
    @Autowired private com.poverty.modules.risk.mapper.RiskAlertMapper riskAlertMapper;
    @Autowired private com.poverty.modules.risk.mapper.SmartMatchMapper smartMatchMapper;

    @ApiOperation("评估家庭风险")
    @PostMapping("/assess/{familyId}")
    public R<RiskAssessment> assess(@PathVariable Long familyId) {
        return R.ok(riskService.assessFamily(familyId));
    }

    @ApiOperation("预警列表")
    @GetMapping("/alerts")
    public R<List<RiskAlert>> alerts() {
        return R.ok(riskService.getUnhandledAlerts());
    }

    @ApiOperation("处理预警")
    @PutMapping("/alert/{id}/handle")
    public R<Void> handleAlert(@PathVariable Long id) {
        riskService.handleAlert(id);
        return R.ok();
    }

    @ApiOperation("风险评估历史")
    @GetMapping("/history/{familyId}")
    public R<List<RiskAssessment>> history(@PathVariable Long familyId) {
        return R.ok(riskService.getHistory(familyId));
    }

    @ApiOperation("预警分页")
    @GetMapping("/alert/page")
    public R<PageResult<RiskAlert>> alertPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer isHandled) {
        LambdaQueryWrapper<RiskAlert> wrapper = new LambdaQueryWrapper<>();
        if (isHandled != null) wrapper.eq(RiskAlert::getIsHandled, isHandled);
        wrapper.orderByDesc(RiskAlert::getCreateTime);
        IPage<RiskAlert> pr = riskAlertMapper.selectPage(new Page<>(page, size), wrapper);
        return R.ok(PageResult.of(pr));
    }

    @ApiOperation("评估记录分页")
    @GetMapping("/assess/page")
    public R<PageResult<RiskAssessment>> assessPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String riskLevel) {
        LambdaQueryWrapper<RiskAssessment> wrapper = new LambdaQueryWrapper<>();
        if (riskLevel != null) wrapper.eq(RiskAssessment::getRiskLevel, riskLevel);
        wrapper.orderByDesc(RiskAssessment::getAssessDate);
        IPage<RiskAssessment> pr = riskAssessmentMapper.selectPage(new Page<>(page, size), wrapper);
        return R.ok(PageResult.of(pr));
    }

    @ApiOperation("预警统计数据")
    @GetMapping("/stats")
    public R<Map<String, Object>> stats() {
        Map<String, Object> r = new HashMap<>();
        r.put("high", riskAlertMapper.selectCount(new LambdaQueryWrapper<RiskAlert>().eq(RiskAlert::getRiskLevel, "高风险").eq(RiskAlert::getIsHandled, 0)));
        r.put("medium", riskAlertMapper.selectCount(new LambdaQueryWrapper<RiskAlert>().eq(RiskAlert::getRiskLevel, "中风险").eq(RiskAlert::getIsHandled, 0)));
        r.put("total", riskAlertMapper.selectCount(new LambdaQueryWrapper<RiskAlert>().eq(RiskAlert::getIsHandled, 0)));
        List<RiskAssessment> all = riskAssessmentMapper.selectList(new LambdaQueryWrapper<RiskAssessment>().orderByDesc(RiskAssessment::getRiskScore).last("LIMIT 5"));
        r.put("topRisks", all);
        return R.ok(r);
    }

    @ApiOperation("智能匹配列表")
    @GetMapping("/matches")
    public R<List<SmartMatch>> getMatches(@RequestParam(required = false) Long familyId) {
        return R.ok(riskService.getMatches(familyId));
    }

    @ApiOperation("生成匹配")
    @PostMapping("/matches/generate/{familyId}")
    public R<List<Map<String, Object>>> generateMatches(@PathVariable Long familyId) {
        return R.ok(riskService.generateMatches(familyId));
    }

    @ApiOperation("接受匹配")
    @PutMapping("/matches/{id}/accept")
    public R<Void> acceptMatch(@PathVariable Long id) {
        riskService.acceptMatch(id);
        return R.ok();
    }
}
