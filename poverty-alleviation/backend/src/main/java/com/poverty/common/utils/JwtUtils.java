package com.poverty.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 生成 Token
     */
    public String generateToken(Long userId, String username, String roleCode) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", roleCode);
        return createToken(claims);
    }

    private String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 从 Token 中解析 Claims
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取用户 ID
     */
    public Long getUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    /**
     * 获取用户名
     */
    public String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    /**
     * 获取角色标识
     */
    public String getRoleCode(String token) {
        return parseToken(token).get("role", String.class);
    }

    /**
     * 验证 Token 是否过期
     */
    public boolean isExpired(String token) {
        try {
            return parseToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return !isExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
