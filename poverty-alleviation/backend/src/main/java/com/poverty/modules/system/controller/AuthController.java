package com.poverty.modules.system.controller;

import com.poverty.common.annotation.RateLimit;
import com.poverty.common.result.R;
import com.poverty.common.utils.JwtUtils;
import com.poverty.modules.system.dto.LoginRequest;
import com.poverty.modules.system.entity.SysUser;
import com.poverty.modules.system.service.SysUserService;
import com.poverty.security.entity.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "系统认证接口")
@RestController("systemAuthController")
@RequestMapping("/api/v1/system/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public R<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            LoginUser loginUser = (LoginUser) authentication.getPrincipal();

            // 生成 JWT Token
            String token = jwtUtils.generateToken(
                    loginUser.getUserId(),
                    loginUser.getUsername(),
                    loginUser.getRoleCode()
            );

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", loginUser.getUserId());
            data.put("username", loginUser.getUsername());
            data.put("realName", loginUser.getRealName());
            data.put("roleCode", loginUser.getRoleCode());

            return R.ok("登录成功", data);
        } catch (BadCredentialsException e) {
            return R.fail("用户名或密码错误");
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("登录失败: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    @ApiOperation("用户注册")
    public R<?> register(@Validated @RequestBody SysUser user) {
        // 注册逻辑
        return R.ok("注册成功");
    }

    @GetMapping("/captcha")
    @ApiOperation("获取验证码")
    public R<?> captcha() {
        // 验证码生成逻辑（后续实现）
        return R.ok("验证码功能待实现");
    }

    @PostMapping("/logout")
    @ApiOperation("退出登录")
    public R<?> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        return R.ok("退出成功");
    }

    @PostMapping("/wx-login")
    @ApiOperation("微信小程序登录")
    @PreAuthorize("permitAll()")
    public R<?> wxLogin(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        if (code == null || code.isEmpty()) {
            return R.fail("code 不能为空");
        }
        try {
            // 调用微信接口获取 openid 和 session_key
            String appId = "wxac00ef37cee29890";
            String secret = "请替换为实际的小程序密钥";
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
            String response = new org.springframework.web.client.RestTemplate().getForObject(url, String.class);
            com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(response);
            String openid = node.has("openid") ? node.get("openid").asText() : null;
            if (openid == null) {
                return R.fail("微信登录失败: " + (node.has("errmsg") ? node.get("errmsg").asText() : "未知错误"));
            }
            // 根据 openid 查找或创建用户（简化：直接返回 openid 供绑定）
            Map<String, Object> data = new HashMap<>();
            data.put("openid", openid);
            return R.ok(data);
        } catch (Exception e) {
            return R.fail("微信登录异常: " + e.getMessage());
        }
    }
}
