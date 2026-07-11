package com.poverty.common.constant;

/**
 * 系统常量定义
 */
public interface Constants {

    /** Token 请求头 */
    String AUTHORIZATION = "Authorization";
    String TOKEN_PREFIX = "Bearer ";

    /** Redis Key 前缀 */
    String REDIS_CAPTCHA_PREFIX = "captcha:";
    String REDIS_TOKEN_PREFIX = "token:";
    String REDIS_RATE_LIMIT_PREFIX = "rate:";

    /** 用户角色标识 */
    String ROLE_ADMIN = "admin";
    String ROLE_CADRE = "cadre";
    String ROLE_POOR = "poor";
    String ROLE_VOLUNTEER = "volunteer";

    /** 通用状态 */
    Integer STATUS_NORMAL = 1;
    Integer STATUS_DISABLED = 0;

    /** 删除标识 */
    Integer DELETED = 1;
    Integer NOT_DELETED = 0;

    /** 接口路径前缀 */
    String API_PREFIX = "/api/v1";
}
