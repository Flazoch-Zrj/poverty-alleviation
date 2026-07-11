package com.poverty.common.utils;

import com.poverty.security.entity.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类 — 获取当前登录用户信息
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户
     */
    public static LoginUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前用户 ID
     */
    public static Long getCurrentUserId() {
        LoginUser user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取当前用户角色
     */
    public static String getCurrentRoleCode() {
        LoginUser user = getCurrentUser();
        return user != null ? user.getRoleCode() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        LoginUser user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 判断是否为管理员
     */
    public static boolean isAdmin() {
        return "admin".equals(getCurrentRoleCode());
    }
}
