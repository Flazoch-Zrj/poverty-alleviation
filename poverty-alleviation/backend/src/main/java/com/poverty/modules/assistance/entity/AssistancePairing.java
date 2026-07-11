package com.poverty.modules.assistance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 帮扶结对实体类
 *
 * @author poverty
 */
@Data
@TableName("assistance_pairing")
public class AssistancePairing implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 结对ID，主键自增
     */
    @TableId(type = IdType.AUTO)
    private Long pairingId;

    /**
     * 干部用户ID
     */
    private Long cadreUserId;

    /**
     * 家庭ID
     */
    private Long familyId;

    /**
     * 结对日期
     */
    private LocalDate pairDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 状态（0：无效，1：有效）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;
}
