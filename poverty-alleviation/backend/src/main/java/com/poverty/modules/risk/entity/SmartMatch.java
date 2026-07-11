package com.poverty.modules.risk.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("smart_match")
public class SmartMatch implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long matchId;
    private Long familyId;
    private String matchType;
    private String targetType;
    private Long targetId;
    private Integer matchScore;
    private String matchReason;
    private Integer isAccepted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
