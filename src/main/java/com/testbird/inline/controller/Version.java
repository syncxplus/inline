package com.testbird.inline.controller;

import com.testbird.inline.util.OutlineApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/version")
public class Version {
    private final OutlineApi outlineApi;
    private final RestTemplate sslTemplate;

    public Version(@Autowired OutlineApi outlineApi, @Autowired RestTemplate sslTemplate) {
        this.outlineApi = outlineApi;
        this.sslTemplate = sslTemplate;
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    private Map version() {
        Map<String, String> map = new HashMap<>();
        map.put("SB_VERSION", String.valueOf(sslTemplate.getForObject(outlineApi.version(), Map.class).get("version")));
        map.put("VERSION", System.getenv("VERSION"));
        return ApiResponse.successfulResponse().setData(map).generate();
    }
}
