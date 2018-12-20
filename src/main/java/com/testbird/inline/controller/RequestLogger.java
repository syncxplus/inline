package com.testbird.inline.controller;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Component
@Order(HIGHEST_PRECEDENCE)
public class RequestLogger extends CommonsRequestLoggingFilter {

    public RequestLogger() {
        setIncludeClientInfo(true);
        setIncludePayload(true);
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        if (!Objects.equals(request.getRequestURI(), "/metrics") && !Objects.equals(request.getRequestURI(), "/version")) {
            super.beforeRequest(request, message);
        }
    }

    @Override
    protected String createMessage(HttpServletRequest request, String prefix, String suffix)  {
        return super.createMessage(request, prefix + request.getMethod() + " ", suffix);
    }
}
