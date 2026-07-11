package com.poverty.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统用户表
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    private String username;

    private String password;

    private String realName;

    private String idCard;

    private String phone;

    private String roleCode;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private LocalDateTime lastLogin;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
