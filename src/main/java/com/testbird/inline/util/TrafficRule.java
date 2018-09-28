package com.testbird.inline.util;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;

@Component
public class TrafficRule {
    private final static Logger logger = LoggerFactory.getLogger(TrafficRule.class);
    private final static int EXEC_TIMEOUT = 1000;
    private final static String LIST_TC = "tc -s -d class show dev eth0";
    private final static String LIST_IPTABLES = "iptables -nvL -t mangle --line-numbers";
    private final static String ADD_TC_FILTER = "tc filter add dev eth0 parent 1:0 prio 1 protocol ip handle %d fw flowid 1:%d0";
    private final static String RM_TC_FILTER  = "tc filter del dev eth0 parent 1:0 prio 1 protocol ip handle %d fw flowid 1:%d0";
    private final static String ADD_IPTABLES_RULE = "iptables -A OUTPUT -t mangle -p tcp --sport %d -j MARK --set-mark %d";
    private final static String RM_IPTABLES_RULE  = "iptables -D OUTPUT -t mangle -p tcp --sport %d -j MARK --set-mark %d";
    private int exitCode;

    public String addTcFilter(int port, int rate) {
        return runShell(String.format(ADD_TC_FILTER, port, rate));
    }

    public String rmTcFilter(int port, int rate) {
        return runShell(String.format(RM_TC_FILTER, port, rate));
    }

    public String addIptablesRule(int port) {
        return runShell(String.format(ADD_IPTABLES_RULE, port, port));
    }

    public String rmIptablesRule(int port) {
        return runShell(String.format(RM_IPTABLES_RULE, port, port));
    }

    public String displayTc() {
        return runShell(LIST_TC);
    }

    public String displayIpTables() {
        return runShell(LIST_IPTABLES);
    }

    public int getExitCode() {
        return exitCode;
    }

    private String runShell(String command) {
        CommandLine commandline = CommandLine.parse(command);
        return runShell(commandline);
    }

    private String runShell(CommandLine commandLine) {
        logger.info(commandLine.toString());
        exitCode = -1024;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream error = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(output, error);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(EXEC_TIMEOUT);
        DefaultExecutor exec = new DefaultExecutor();
        exec.setStreamHandler(streamHandler);
        exec.setWatchdog(watchdog);
        try {
            exitCode = exec.execute(commandLine);
            logger.info("exit code: " + exitCode);
            String e = error.toString();
            if (!StringUtils.isEmpty(e)) {
                logger.error(e);
                return e;
            }
            String o = output.toString();
            logger.info(o);
            return o;
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            return t.getClass().getName() + ":" + t.getMessage();
        }
    }
}
