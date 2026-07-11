package com.poverty.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("risk_assessment")
public class RiskAssessment implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long riskId;
    private Long familyId;
    private LocalDate assessDate;
    private Integer riskScore;
    private String riskLevel;
    private Integer incomeStability;
    private Integer healthStatus;
    private Integer employmentStatus;
    private Integer educationStatus;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
