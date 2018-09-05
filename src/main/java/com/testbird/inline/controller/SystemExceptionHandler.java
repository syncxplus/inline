package com.testbird.inline.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class SystemExceptionHandler {
    @ExceptionHandler
    public Map response(Throwable t) {
        return ApiResponse.failedResponse(t.getMessage()).setException(t.getClass().getName()).generate();
    }
}
