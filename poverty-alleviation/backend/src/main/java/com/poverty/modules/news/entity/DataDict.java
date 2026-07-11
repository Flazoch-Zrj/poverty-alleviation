package com.poverty.modules.news.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("data_dict")
public class DataDict implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long dictId;

    private String dictType;

    private String dictLabel;

    private String dictValue;

    private Integer sortOrder;

    private Integer status;

    private String remark;
}
