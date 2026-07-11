package com.poverty.modules.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 扶贫项目实体类
 *
 * @author poverty
 */
@Data
@TableName("poverty_project")
public class PovertyProject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @TableId(value = "project_id", type = IdType.AUTO)
    private Long projectId;

    /**
     * 项目名称
     */
    @TableField("project_name")
    private String projectName;

    /**
     * 家庭ID
     */
    @TableField("family_id")
    private Long familyId;

    /**
     * 申请人ID
     */
    @TableField("proposer_id")
    private Long proposerId;

    /**
     * 项目预算
     */
    @TableField("budget")
    private BigDecimal budget;

    /**
     * 已筹集金额
     */
    @TableField("raised_amount")
    private BigDecimal raisedAmount;

    /**
     * 项目描述
     */
    @TableField("description")
    private String description;

    /**
     * 项目状态（0待审核/1已通过/2已驳回/3进行中/4已完成）
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
