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
    private String display() {
        StringBuilder sb = new StringBuilder("********** tc **********").append(System.lineSeparator());
        String tc = trafficRule.displayTc();
        if (trafficRule.getExitCode() == 0) {
            sb.append(tc);
        } else {
            sb.append("error:").append(trafficRule.getExitCode());
        }
        sb.append(System.lineSeparator()).append("********** iptables **********").append(System.lineSeparator());
        String ipt = trafficRule.displayIpTables();
        if (trafficRule.getExitCode() == 0) {
            sb.append(ipt);
        } else {
            sb.append("error:").append(trafficRule.getExitCode());
        }
        return sb.append(System.lineSeparator()).toString();
    }
}
