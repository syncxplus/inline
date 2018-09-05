package com.testbird.inline.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotFoundController implements ErrorController {
    private final static String PATH = "/error";

    @RequestMapping(PATH)
    private Object response() {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ApiResponse.failedResponse(status.name()).generate();
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
