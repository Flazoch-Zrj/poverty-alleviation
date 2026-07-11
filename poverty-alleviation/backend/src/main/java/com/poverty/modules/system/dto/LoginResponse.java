package com.poverty.modules.system.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应 DTO
 */
@Data
public class LoginResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token;
    private Long userId;
    private String username;
    private String realName;
    private String roleCode;
}
