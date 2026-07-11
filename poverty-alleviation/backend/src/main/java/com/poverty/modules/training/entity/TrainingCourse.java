package com.poverty.modules.training.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("training_course")
public class TrainingCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long courseId;

    private String title;

    private String content;

    private String trainer;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

    private Long familyId;

    private Integer maxEnroll;

    private Integer enrolledCount;

    private Integer status;

    private Long publisherId;
}
