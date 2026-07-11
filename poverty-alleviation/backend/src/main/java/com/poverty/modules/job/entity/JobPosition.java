package com.poverty.modules.job.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("job_position")
public class JobPosition implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long jobId;

    private Long publisherId;

    private Long familyId;

    private String title;

    private String company;

    private String requirements;

    private String salaryRange;

    private String workplace;

    private String contact;

    private Integer isValid;

    private LocalDateTime publishTime;
}
