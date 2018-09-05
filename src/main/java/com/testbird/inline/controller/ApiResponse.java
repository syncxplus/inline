package com.testbird.inline.controller;

import java.util.HashMap;
import java.util.Map;

class ApiResponse {
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
        return data;
    }
}
