package com.poverty.modules.volunteer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("volunteer_level")
public class VolunteerLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer levelId;

    private String levelName;

    private Integer minScore;

    private String badgeIcon;
}
