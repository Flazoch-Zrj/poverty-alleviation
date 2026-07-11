package com.poverty.modules.visit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 走访记录表
 */
@Data
@TableName("visit_record")
public class VisitRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

    private Long cadreUserId;

    private Long volunteerUserId;

    private Long familyId;

    private LocalDate visitDate;

    private String content;

    private String photos;

    private String nextPlan;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String familyName;
}
