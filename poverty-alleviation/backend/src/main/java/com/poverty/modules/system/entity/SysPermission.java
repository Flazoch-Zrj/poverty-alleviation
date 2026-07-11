package com.poverty.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 权限表
 */
@Data
@TableName("sys_permission")
public class SysPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "perm_id", type = IdType.AUTO)
    private Long permId;

    private String permName;

    private String permCode;

    private Long parentId;

    private Integer type;
}
