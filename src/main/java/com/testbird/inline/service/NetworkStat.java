package com.testbird.inline.service;

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
import java.util.Objects;

@Service
public class NetworkStat {
    private static final Logger logger = LoggerFactory.getLogger(NetworkStat.class);
    private static final String COMMAND = "ifstat eth0";
    private final NetworkGauge networkGauge;

    public NetworkStat(@Autowired NetworkGauge networkGauge) {
        this.networkGauge = networkGauge;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void calcNetworkStat() {
        if (Objects.equals(VersionGauge.getOS(), "linux")) {
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
                    String[] lines = s.split("\n");
                    if (lines.length > 3) {
                        String[] eth0 = lines[3].split("\\s+");
                        if (eth0.length > 7) {
                            int rxStat = parseStatBytes(eth0[5].toLowerCase());
                            int txStat = parseStatBytes(eth0[7].toLowerCase());
                            if (rxStat < 0) rxStat = 0;
                            if (txStat < 0) txStat = 0;
                            networkGauge.get().labels("rx").set(rxStat);
                            networkGauge.get().labels("tx").set(txStat);
                            networkGauge.get().labels("all").set(rxStat + txStat);
                        }
                    }
                    /*ObjectMapper mapper = new ObjectMapper();
                    Map<String, Map<String, Map>> map = mapper.readValue(s, Map.class);
                    if (map.size() == 1) {
                        Map<String, Map> stat = map.get(map.keySet().iterator().next());
                        if (stat.containsKey("eth0")) {
                            Map eth0 = stat.get("eth0");
                            logger.debug(String.valueOf(eth0.get("rx_bytes")));
                            logger.debug(String.valueOf(eth0.get("tx_bytes")));
                            //double prx = networkGauge.get().labels("rx").get();
                            double rx = Double.parseDouble(String.valueOf(eth0.get("rx_bytes")));
                            *//*if (prx > 0 || rx > prx) {
                                rx -= prx;
                            }*//*
                            //double ptx = networkGauge.get().labels("tx").get();
                            double tx = Double.parseDouble(String.valueOf(eth0.get("tx_bytes")));
                            *//*if (ptx > 0 || tx > ptx) {
                                tx -= ptx;
                            }*//*
                            networkGauge.get().labels("rx").set(rx);
                            networkGauge.get().labels("tx").set(tx);
                            networkGauge.get().labels("all").set(rx + tx);
                        }
                    }*/
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private static int parseStatBytes(String stat) {
        try {
            if (stat.endsWith("m")) {
                return Integer.parseInt(stat.substring(0, stat.length() - 1)) * 1024 *1024;
            } else if (stat.endsWith("k")) {
                return Integer.parseInt(stat.substring(0, stat.length() - 1)) * 1024;
            } else {
                return Integer.parseInt(stat);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return  0;
        }
    }
}
