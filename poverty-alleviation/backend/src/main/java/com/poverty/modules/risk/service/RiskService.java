package com.poverty.modules.risk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.poverty.modules.poverty.entity.PovertyFamily;
import com.poverty.modules.poverty.service.PovertyFamilyService;
import com.poverty.modules.risk.entity.RiskAlert;
import com.poverty.modules.risk.entity.RiskAssessment;
import com.poverty.modules.risk.entity.SmartMatch;
import com.poverty.modules.risk.mapper.RiskAlertMapper;
import com.poverty.modules.risk.mapper.RiskAssessmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RiskService {

    @Autowired private RiskAssessmentMapper riskAssessmentMapper;
    @Autowired private RiskAlertMapper riskAlertMapper;
    @Autowired private com.poverty.modules.risk.mapper.SmartMatchMapper matchMapper;
    @Autowired private PovertyFamilyService povertyFamilyService;

    public RiskAssessment assessFamily(Long familyId) {
        PovertyFamily family = povertyFamilyService.getById(familyId);
        if (family == null) return null;

        int score = 0, income = 50, health = 50, employment = 50, education = 80;

        // 收入评估
        if (family.getAnnualIncome() != null) {
            double incomeVal = family.getAnnualIncome().doubleValue();
            if (incomeVal < 4000) { score += 35; income = 20; }
            else if (incomeVal < 6000) { score += 25; income = 40; }
            else if (incomeVal < 8000) { score += 15; income = 60; }
            else { score += 5; income = 85; }
        }

        // 贫困等级
        if ("特困供养".equals(family.getPovertyLevel())) { score += 25; health = 30; }
        else if ("低保贫困户".equals(family.getPovertyLevel())) { score += 15; health = 50; }
        else if ("一般贫困户".equals(family.getPovertyLevel())) { score += 10; health = 65; }

        // 状态
        if ("已返贫".equals(family.getStatus())) { score += 30; employment = 30; }
        else if ("已脱贫".equals(family.getStatus())) { score -= 15; employment = 80; }

        String level;
        if (score >= 80) level = "高风险";
        else if (score >= 50) level = "中风险";
        else level = "低风险";

        RiskAssessment ra = new RiskAssessment();
        ra.setFamilyId(familyId);
        ra.setAssessDate(LocalDate.now());
        ra.setRiskScore(score);
        ra.setRiskLevel(level);
        ra.setIncomeStability(income);
        ra.setHealthStatus(health);
        ra.setEmploymentStatus(employment);
        ra.setEducationStatus(education);
        riskAssessmentMapper.insert(ra);

        // 高风险自动生成预警
        if ("高风险".equals(level)) {
            RiskAlert alert = new RiskAlert();
            alert.setFamilyId(familyId);
            alert.setRiskLevel(level);
            alert.setContent(family.getHouseholderName() + "家庭返贫风险较高（评分" + score + "），建议立即走访");
            alert.setIsHandled(0);
            riskAlertMapper.insert(alert);
        }

        return ra;
    }

    public List<SmartMatch> getMatches(Long familyId) {
        LambdaQueryWrapper<SmartMatch> w = new LambdaQueryWrapper<>();
        if (familyId != null) w.eq(SmartMatch::getFamilyId, familyId);
        w.orderByDesc(SmartMatch::getMatchScore);
        return matchMapper.selectList(w);
    }

    public void acceptMatch(Long matchId) {
        SmartMatch m = new SmartMatch(); m.setMatchId(matchId); m.setIsAccepted(1);
        matchMapper.updateById(m);
    }

    public List<Map<String, Object>> generateMatches(Long familyId) {
        List<Map<String, Object>> results = new ArrayList<>();
        PovertyFamily f = povertyFamilyService.getById(familyId);
        if (f == null) return results;
        double income = f.getAnnualIncome() != null ? f.getAnnualIncome().doubleValue() : 0;
        String level = f.getPovertyLevel() != null ? f.getPovertyLevel() : "";

        // 收入低 → 推荐产业项目 + 就业
        if (income < 6000) {
            results.add(makeMatch(familyId, "project", null, 85, "年收入偏低，推荐产业帮扶项目"));
            results.add(makeMatch(familyId, "job", null, 75, "可推荐就业岗位增加收入"));
        }
        // 因病 → 推荐医疗救助
        if (level.contains("病")) {
            results.add(makeMatch(familyId, "project", null, 80, "因病致贫，推荐医疗帮扶项目"));
        }
        // 缺技术 → 推荐培训
        results.add(makeMatch(familyId, "training", null, 70, "推荐参加技能培训提升就业能力"));

        for (Map<String, Object> r : results) {
            SmartMatch m = new SmartMatch();
            m.setFamilyId(familyId);
            m.setMatchType((String) r.get("type"));
            m.setMatchScore((Integer) r.get("score"));
            m.setMatchReason((String) r.get("reason"));
            m.setIsAccepted(0);
            matchMapper.insert(m);
            r.put("matchId", m.getMatchId());
        }
        return results;
    }

    private Map<String, Object> makeMatch(Long fid, String type, Long tid, int score, String reason) {
        Map<String, Object> m = new HashMap<>();
        m.put("familyId", fid); m.put("type", type); m.put("targetId", tid);
        m.put("score", score); m.put("reason", reason);
        return m;
    }

    public List<RiskAlert> getUnhandledAlerts() {
        return riskAlertMapper.selectList(new LambdaQueryWrapper<RiskAlert>()
                .eq(RiskAlert::getIsHandled, 0).orderByDesc(RiskAlert::getCreateTime));
    }

    public void handleAlert(Long alertId) {
        RiskAlert a = new RiskAlert(); a.setAlertId(alertId); a.setIsHandled(1);
        riskAlertMapper.updateById(a);
    }

    public List<RiskAssessment> getHistory(Long familyId) {
        return riskAssessmentMapper.selectList(new LambdaQueryWrapper<RiskAssessment>()
                .eq(RiskAssessment::getFamilyId, familyId).orderByDesc(RiskAssessment::getAssessDate));
    }
}
