package com.poverty.security.handler;

import com.poverty.common.result.R;
import com.poverty.common.utils.JwtUtils;
import com.poverty.security.entity.LoginUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录成功处理器
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
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

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(R.ok("登录成功", data)));
    }
}
