package com.testbird.inline.controller;

import com.testbird.inline.util.TrafficRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rule")
public class Rule {
    private final TrafficRule trafficRule;

    public Rule(@Autowired TrafficRule trafficRule) {
        this.trafficRule = trafficRule;
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    private String list() {
        StringBuilder sb = new StringBuilder();
        sb.append("********** tc **********").append(System.lineSeparator())
                .append(trafficRule.listTc()).append(System.lineSeparator())
                .append("********** iptables **********").append(System.lineSeparator())
                .append(trafficRule.listIptables()).append(System.lineSeparator());
        return sb.toString();
    }
}
