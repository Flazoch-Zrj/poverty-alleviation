package com.poverty.modules.volunteer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("volunteer_record")
public class VolunteerRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long recordId;

    private Long activityId;

    private Long volunteerUserId;

    private Long familyId;

    private LocalDateTime signInTime;

    private LocalDateTime signOutTime;

    private BigDecimal serviceHours;

    private String serviceContent;

    private String photos;

    private String beneficiaryFeedback;

    private Integer beneficiaryRating;

    private Integer status;
}
