package com.testbird.inline.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class RequestLogger implements Filter {
    private final static Logger logger = LoggerFactory.getLogger(RequestLogger.class);

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) request;
        logger.info("REQUEST: {} {}", r.getMethod(), r.getRequestURI());
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
