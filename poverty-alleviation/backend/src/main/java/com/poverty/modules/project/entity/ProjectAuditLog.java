package com.poverty.modules.project.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 项目审计日志表
 */
@Data
@TableName("project_audit_log")
public class ProjectAuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    private Long projectId;

    private Long auditorId;

    private String action;

    private String comment;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
