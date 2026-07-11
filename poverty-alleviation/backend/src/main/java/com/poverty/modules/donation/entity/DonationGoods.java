package com.poverty.modules.donation.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("donation_goods")
public class DonationGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long goodsDonationId;

    private String donorName;

    private String goodsName;

    private Integer quantity;

    private String unit;

    private Long needId;

    private String logisticsInfo;

    private Long recorderId;

    private Long receiveFamilyId;

    private Long distributedBy;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime donateTime;
}
