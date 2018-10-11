package com.testbird.inline.controller;

import com.testbird.inline.metrics.HttpRequestCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Component
@Order(HIGHEST_PRECEDENCE)
public class RequestLogger extends CommonsRequestLoggingFilter {
    @Autowired
    private HttpRequestCounter httpRequestCounter;

    public RequestLogger() {
        //setIncludeQueryString(true);
        setIncludeClientInfo(true);
        //setIncludePayload(true);
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        super.beforeRequest(request, message);
        if (!Objects.equals(request.getRequestURI(), "/metrics")) {
            httpRequestCounter.get().labels(request.getMethod(), request.getRequestURI()).inc();
        }
    }

    @Override
    protected String createMessage(HttpServletRequest request, String prefix, String suffix)  {
        return super.createMessage(request, prefix + request.getMethod() + " ", suffix);
    }
}
