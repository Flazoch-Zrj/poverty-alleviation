package com.poverty.modules.job.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("job_application")
public class JobApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long applyId;

    private Long jobId;

    private Long applicantUserId;

    private Integer applyStatus;

    private String remark;

    private LocalDateTime applyTime;

    @TableField(exist = false)
    private String jobTitle;
}
