package com.testbird.inline.service;

import com.sun.management.OperatingSystemMXBean;
import com.testbird.inline.metrics.MemoryGauge;
import com.testbird.inline.metrics.VersionGauge;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.lang.management.ManagementFactory;
import java.util.Objects;

@Service
public class MemoryUsageStatus {
    private static final Logger logger = LoggerFactory.getLogger(MemoryUsageStatus.class);
    private static final String COMMAND = "free";
    private final MemoryGauge memoryGauge;

    public MemoryUsageStatus(@Autowired MemoryGauge memoryGauge) {
        this.memoryGauge = memoryGauge;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void calc() {
        memoryGauge.get().set(getMemoryUsage());
    }

    public static double getMemoryUsage() {
        if (Objects.equals(VersionGauge.getOS(), "centos")) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ByteArrayOutputStream error = new ByteArrayOutputStream();
            PumpStreamHandler streamHandler = new PumpStreamHandler(output, error);
            ExecuteWatchdog watchdog = new ExecuteWatchdog(1000);
            DefaultExecutor exec = new DefaultExecutor();
            exec.setStreamHandler(streamHandler);
            exec.setWatchdog(watchdog);
            try {
                int exitCode = exec.execute(CommandLine.parse(COMMAND));
                if (exitCode == 0) {
                    String[] s = output.toString().split("\\s+");
                    int i = 0;
                    while (!Objects.equals(s[i], "Mem:")) {
                        i ++;
                    }
                    if (i < (s.length - 2)) {
                        logger.info("memory usage: {} / {}", s[i + 2], s[i + 1]);
                        return Double.parseDouble(s[i + 2]) / Double.parseDouble(s[i + 1]);
                    }
                }
            } catch (Throwable t) {
                //do nothing
            }
        }
        OperatingSystemMXBean mxBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double free = mxBean.getFreePhysicalMemorySize();
        double total = mxBean.getTotalPhysicalMemorySize();
        logger.info("memory free: {} / {}", free, total);
        return (total - free) / total;
    }
}
