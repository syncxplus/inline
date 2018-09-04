package com.testbird.inline.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Objects;

@Component
public class ApiInterceptor extends HandlerInterceptorAdapter {
    private final static Logger logger = LoggerFactory.getLogger(ApiInterceptor.class);
    @Value("${api-key}")
    private String apiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (Objects.equals(request.getRemoteAddr(), "127.0.0.1")) {
            return true;
        }
        String authorization = request.getHeader("authorization");
        if (!StringUtils.isEmpty(authorization)) {
            String[] tokenArray = authorization.split(" ");
            if (tokenArray.length > 1) {
                String token = new String(Base64.getDecoder().decode(tokenArray[1]));
                return Objects.equals(apiKey, token);
            }
        }
        return false;
    }
}
