package com.poverty.modules.training.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("training_enrollment")
public class TrainingEnrollment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long enrollId;

    private Long courseId;

    private Long userId;

    private LocalDateTime signInTime;

    private Integer status;

    private LocalDateTime enrollTime;

    @TableField(exist = false)
    private String courseTitle;
}
