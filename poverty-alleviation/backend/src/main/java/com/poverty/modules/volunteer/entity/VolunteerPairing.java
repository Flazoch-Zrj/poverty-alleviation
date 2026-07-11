package com.poverty.modules.volunteer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@TableName("volunteer_pairing")
public class VolunteerPairing implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long pairingId;

    private Long volunteerUserId;

    private Long familyId;

    private LocalDate pairDate;

    private LocalDate endDate;

    private String status;

    private String remark;
}
