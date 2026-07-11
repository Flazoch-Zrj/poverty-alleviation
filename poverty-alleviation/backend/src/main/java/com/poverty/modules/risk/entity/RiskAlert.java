package com.poverty.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("risk_alert")
public class RiskAlert implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long alertId;
    private Long familyId;
    private String riskLevel;
    private String content;
    private Integer isHandled;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
