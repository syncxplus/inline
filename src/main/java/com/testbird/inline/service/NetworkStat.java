package com.testbird.inline.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testbird.inline.metrics.NetworkGauge;
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
import java.util.Map;
import java.util.Objects;

@Service
public class NetworkStat {
    private static final Logger logger = LoggerFactory.getLogger(NetworkStat.class);
    private static final String COMMAND = "ifstat -j eth0";
    private final NetworkGauge networkGauge;

    public NetworkStat(@Autowired NetworkGauge networkGauge) {
        this.networkGauge = networkGauge;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void calcNetworkStat() {
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
                    String s = output.toString().trim();
                    logger.info(s);
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Map<String, Map>> map = mapper.readValue(s, Map.class);
                    if (map.size() == 1) {
                        Map<String, Map> stat = map.get(map.keySet().iterator().next());
                        if (stat.containsKey("eth0")) {
                            Map eth0 = stat.get("eth0");
                            logger.debug(String.valueOf(eth0.get("rx_bytes")));
                            logger.debug(String.valueOf(eth0.get("tx_bytes")));
                            double rx = Double.parseDouble(String.valueOf(eth0.get("rx_bytes")));
                            double prx = networkGauge.get().labels("rx").get();
                            if (prx < 0 || rx < prx) {
                                networkGauge.get().labels("rx").set(rx);
                            } else {
                                networkGauge.get().labels("rx").set(rx - prx);
                            }
                            double tx = Double.parseDouble(String.valueOf(eth0.get("tx_bytes")));
                            double ptx = networkGauge.get().labels("tx").get();
                            if (ptx < 0 || tx < ptx) {
                                networkGauge.get().labels("tx").set(tx);
                            } else {
                                networkGauge.get().labels("tx").set(tx - ptx);
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
