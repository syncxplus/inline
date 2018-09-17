package com.testbird.inline.controller;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rule")
public class RuleController {
    private final static Logger logger = LoggerFactory.getLogger(RuleController.class);
    private final static String STEP_1 = "tc class add dev eth0 parent 1:1 classid 1:5 htb rate %dKbit ceil %dKbit prio 1";
    private final static String STEP_2 = "tc filter add dev eth0 parent 1:0 prio 1 protocol ip handle %d fw flowid 1:5";
    private final static String STEP_3 = "iptables -A OUTPUT -t mangle -p tcp --sport %d -j MARK --set-mark %d";

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    private Object addRule(@RequestParam("rate") Integer rate, @RequestParam("port") Integer port) {
        Map<String, Object> map = new HashMap<>();
        map.put("step_1", runShell(String.format(STEP_1, rate, rate), 5000));
        map.put("step_2", runShell(String.format(STEP_2, port), 5000));
        map.put("step_3", runShell(String.format(STEP_3, port, port), 5000));
        return ApiResponse.successfulResponse().setData(map).generate();
    }

    private Object deleteRule() {
        return null;
    }

    private static List<String> runShell(String command, long milliseconds) {
        CommandLine commandline = CommandLine.parse(command);
        return runShell(commandline, milliseconds);
    }

    private static List<String> runShell(CommandLine commandLine, long milliseconds) {
        logger.info(commandLine.toString());
        List<String> results = new ArrayList<>();
        try {
            DefaultExecutor exec = new DefaultExecutor();
            exec.setExitValues(null);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ByteArrayOutputStream error = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(output, error);
            exec.setStreamHandler(streamHandler);
            ExecuteWatchdog watchdog = new ExecuteWatchdog(milliseconds);
            exec.setWatchdog(watchdog);
            int exit = exec.execute(commandLine);
            String charset = "UTF-8";
            results.add(output.toString(charset));
            results.add(error.toString(charset));
            results.add(String.valueOf(exit));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
