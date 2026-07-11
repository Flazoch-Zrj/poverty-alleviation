package com.poverty.modules.volunteer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@TableName("volunteer_certificate")
public class VolunteerCertificate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long certId;

    private Long userId;

    private String certType;

    private Long sourceId;

    private String certName;

    private String certNumber;

    private LocalDate issueDate;

    private String fileUrl;
}
