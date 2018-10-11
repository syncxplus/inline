package com.testbird.inline.service;

import com.sun.management.OperatingSystemMXBean;
import com.testbird.inline.metrics.MemoryGauge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;

@Service
public class MemoryUsageStatus {
    private final MemoryGauge memoryGauge;

    public MemoryUsageStatus(@Autowired MemoryGauge memoryGauge) {
        this.memoryGauge = memoryGauge;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void calc() {
        memoryGauge.get().set(getMemoryUsage());
    }

    public static double getMemoryUsage() {
        OperatingSystemMXBean mxBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double free = mxBean.getFreePhysicalMemorySize();
        double total = mxBean.getTotalPhysicalMemorySize();
        return (total - free) / total;
    }
}
