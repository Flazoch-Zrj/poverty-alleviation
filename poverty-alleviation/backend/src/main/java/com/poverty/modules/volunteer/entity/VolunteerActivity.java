package com.poverty.modules.volunteer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("volunteer_activity")
public class VolunteerActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long activityId;

    private String title;

    private String description;

    private String activityType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

    private String coverImage;

    private String contactPhone;

    private Integer difficulty;

    private Long familyId;

    private Long organizerId;

    private Integer needVolunteers;

    private Integer registeredCount;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String familyName;
}
