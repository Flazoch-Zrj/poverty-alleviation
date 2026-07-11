package com.poverty.modules.assistance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 帮扶措施实体类
 */
@Data
@TableName("assistance_measure")
public class AssistanceMeasure implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 措施ID，自增主键
     */
    @TableId(value = "measure_id", type = IdType.AUTO)
    private Long measureId;

    /**
     * 结对ID
     */
    @TableField("pairing_id")
    private Long pairingId;

    /**
     * 家庭ID
     */
    @TableField("family_id")
    private Long familyId;

    /**
     * 措施类型
     */
    @TableField("measure_type")
    private String measureType;

    /**
     * 措施内容
     */
    @TableField("content")
    private String content;

    /**
     * 目标金额
     */
    @TableField("target_amount")
    private BigDecimal targetAmount;

    /**
     * 实际金额
     */
    @TableField("actual_amount")
    private BigDecimal actualAmount;

    /**
     * 进度（0-100）
     */
    @TableField("progress")
    private Integer progress;

    /**
     * 状态：0-未启动，1-进行中，2-已完成，3-已取消
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
