package com.poverty.modules.need.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("needs_publish")
public class NeedsPublish implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long needId;

    private Long familyId;

    private Long publisherId;

    private String needType;

    private String title;

    private String description;

    private BigDecimal targetAmount;

    private BigDecimal actualAmount;

    private Long volunteerId;

    private LocalDateTime volunteerConfirmTime;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
