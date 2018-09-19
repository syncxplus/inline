package com.testbird.inline.controller;

import com.testbird.inline.util.TrafficRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rule")
public class Rule {
    private final TrafficRule trafficRule;

    public Rule(@Autowired TrafficRule trafficRule) {
        this.trafficRule = trafficRule;
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    private Object list() {
        boolean result;
        Map<String, Object> map = new HashMap<>();
        map.put("tc", trafficRule.listTc());
        result = trafficRule.isSuccess();
        map.put("iptables", trafficRule.listIptables());
        result &= trafficRule.isSuccess();
        if (result) {
            return ApiResponse.successfulResponse().setData(map).generate();
        } else {
            return ApiResponse.failedResponse(null).setData(map).generate();
        }
    }
}
