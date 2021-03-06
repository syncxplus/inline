package com.testbird.inline.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testbird.inline.metrics.*;
import com.testbird.inline.util.OutlineApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OutlineStatus {
    private final static Logger logger = LoggerFactory.getLogger(OutlineStatus.class);
    private final OutlineApi outlineApi;
    private RestTemplate sslTemplate;
    @Autowired
    private OutlineGauge outlineGauge;
    @Autowired
    private VersionGauge versionGauge;
    @Autowired
    private UserGauge userGauge;
    @Autowired
    private PortGauge portGauge;
    @Autowired
    private RateGauge rateGauge;

    public OutlineStatus(@Autowired OutlineApi outlineApi, @Autowired RestTemplate sslTemplate) {
        this.outlineApi = outlineApi;
        this.sslTemplate = sslTemplate;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void check() {
        Map map = null;
        try {
            ResponseEntity<Map> response = sslTemplate.getForEntity(outlineApi.info(), Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                outlineGauge.get().set(1);
                map = response.getBody();
                logger.info(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(map));
            } else {
                setInvalid();
            }
        } catch (Throwable t) {
            logger.error("check outline status error: {}", t.getClass().getName(), t);
            setInvalid();
        }
        if (map != null) {
            versionGauge.get().labels(VersionGauge.VERSION_LABEL_SHADOWBOX)
                    .set(VersionGauge.parseVersion(String.valueOf(map.get("version"))));
            userGauge.get()
                    .set(Double.parseDouble(String.valueOf(map.getOrDefault("userCount", 0))));
            portGauge.get()
                    .set(Double.parseDouble(String.valueOf(map.getOrDefault("activePortCount", 0))));
            rateGauge.get()
                    .labels("20", "user")
                    .set(Double.parseDouble(String.valueOf(map.getOrDefault("userRate20Count", 0))));
            rateGauge.get()
                    .labels("80", "user")
                    .set(Double.parseDouble(String.valueOf(map.getOrDefault("userRate80Count", 0))));
            rateGauge.get()
                    .labels("20", "port")
                    .set(Double.parseDouble(String.valueOf(map.getOrDefault("activePortRate20Count", 0))));
            rateGauge.get()
                    .labels("80", "port")
                    .set(Double.parseDouble(String.valueOf(map.getOrDefault("activePortRate80Count", 0))));
        }
    }

    private void setInvalid() {
        outlineGauge.get().set(0);
        versionGauge.get().labels(VersionGauge.VERSION_LABEL_SHADOWBOX).set(0);
        userGauge.get().set(0);
        portGauge.get().set(0);
        rateGauge.get().labels("20").set(0);
        rateGauge.get().labels("80").set(0);
    }
}
