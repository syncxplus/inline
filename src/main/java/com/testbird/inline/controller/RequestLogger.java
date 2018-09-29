package com.testbird.inline.controller;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Component
@Order(HIGHEST_PRECEDENCE)
public class RequestLogger extends CommonsRequestLoggingFilter {
    public RequestLogger() {
        setIncludeQueryString(true);
        setIncludeClientInfo(true);
        setIncludePayload(true);
    }

    @Override
    protected String createMessage(HttpServletRequest request, String prefix, String suffix)  {
        return super.createMessage(request, prefix + request.getMethod() + " ", suffix);
    }
}
