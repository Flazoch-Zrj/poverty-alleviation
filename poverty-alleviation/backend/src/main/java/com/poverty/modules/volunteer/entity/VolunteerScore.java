package com.poverty.modules.volunteer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("volunteer_score")
public class VolunteerScore implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long scoreId;

    private Long userId;

    private String scoreType;

    private Integer score;

    private Long sourceId;

    private String sourceType;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
