package com.testbird.inline.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class VersionGauge {
    private static final String OS_NAME = "os.name";
    private static final String OS_VERSION = "os.version";
    private static final String OS_RELEASE = "cat /etc/redhat-release";
    private static final String TARGET_OS = "centos";
    private static final String FALLBACK_OS = "linux";
    private static final String osName;
    private static final double osVersion;
    private final Gauge gauge;

    static {
        String name = System.getProperty(OS_NAME);
        if (name.toLowerCase().contains(FALLBACK_OS)) {
            String redhat = getRedhatRelease();
            if (redhat.toLowerCase().contains(TARGET_OS)) {
                osName = TARGET_OS;
                osVersion = parseVersion(redhat);
            } else {
                osName = redhat.replaceAll(" ", "_");
                osVersion = parseVersion(System.getProperty(OS_VERSION));
            }

        } else {
            osName = name.replaceAll(" ", "_");
            osVersion = parseVersion(System.getProperty(OS_VERSION));
        }
    }

    public VersionGauge(@Autowired CollectorRegistry collectorRegistry) {
        gauge = Gauge.build()
                .name("server_version")
                .help("server version number")
                .labelNames("name")
                .register(collectorRegistry);
        gauge.labels(osName).set(osVersion);
    }

    public Gauge get() {
        return gauge;
    }

    public static double parseVersion(String version) {
        Matcher m = Pattern.compile("(\\d+\\.\\d+)").matcher(version);
        if (m.find()) {
            return Double.parseDouble(m.group(0));
        } else {
            m = Pattern.compile("(\\d+)").matcher(version);
            if (m.find()) {
                return Double.parseDouble(m.group(0));
            }
        }
        return -1d;
    }

    private static String getRedhatRelease() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream error = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(output, error);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(1000);
        DefaultExecutor exec = new DefaultExecutor();
        exec.setStreamHandler(streamHandler);
        exec.setWatchdog(watchdog);
        try {
            int exitCode = exec.execute(CommandLine.parse(OS_RELEASE));
            if (exitCode == 0) {
                return output.toString();
            }
        } catch (Throwable t) {
            //do nothing
        }
        return FALLBACK_OS;
    }
}
