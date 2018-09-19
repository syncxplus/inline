package com.testbird.inline.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

class ApiResponse {
    private final static Logger logger = LoggerFactory.getLogger(ApiResponse.class);
    private final boolean status;
    private final String message;
    private String exception;
    private Map data;

    private ApiResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    static ApiResponse successfulResponse() {
        return new ApiResponse(true, "success");
    }

    static ApiResponse failedResponse(String message) {
        return new ApiResponse(false, message);
    }

    ApiResponse setException(String exception) {
        this.exception = exception;
        return this;
    }

    ApiResponse setData(Map map) {
        data = map;
        return this;
    }

    @SuppressWarnings("unchecked")
    Map generate() {
        if (data == null) {
            data = new HashMap();
        }
        data.put("status", status);
        data.put("message", message);
        if (exception != null) {
            data.put("exception", exception);
        }
        try {
            logger.info(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(data));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return data;
    }
}
