package com.poverty.modules.donation.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("donation_money")
public class DonationMoney implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long moneyDonationId;

    private String donorName;

    private BigDecimal amount;

    private String paymentMethod;

    private String transactionId;

    private Long recorderId;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime donateTime;

    private Integer isAnonymous;

    private Long needId;

    private Long projectId;
}
