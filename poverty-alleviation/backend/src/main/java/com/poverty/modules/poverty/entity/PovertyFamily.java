package com.poverty.modules.poverty.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 贫困户家庭档案表
 */
@Data
@TableName("poverty_family")
public class PovertyFamily implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "family_id", type = IdType.AUTO)
    private Long familyId;

    private String familyCode;

    private Long householderId;

    private String householderName;

    private String idCard;

    private String province;
    private String city;
    private String district;
    private String town;
    private String village;
    private String address;

    private Integer familySize;

    private BigDecimal annualIncome;

    private String povertyCauseCode;

    private String povertyLevel;

    private String status;

    private LocalDate filingDate;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
