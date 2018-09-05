package com.testbird.inline.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testbird.inline.util.OutlineApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class OutlineStatus {
    private final static Logger logger = LoggerFactory.getLogger(OutlineStatus.class);
    private final OutlineApi outlineApi;
    private final RestTemplate sslTemplate;

    public OutlineStatus(@Autowired OutlineApi outlineApi, @Autowired RestTemplate sslTemplate) {
        this.outlineApi = outlineApi;
        this.sslTemplate = sslTemplate;
    }

    @Scheduled(cron = "0 0,30 * * * *")
    public void check() {
        String url = outlineApi.serverStatus();
        logger.info("check outline status: {}", url);
        try {
            ResponseEntity<Map> response = sslTemplate.getForEntity(url, Map.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("status code: {}", response.getStatusCode());
            } else {
                Map status = response.getBody();
                logger.info(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(status));
                boolean metrics = (boolean) status.get("metricsEnabled");
                if (!metrics) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    Map<String, Boolean> params= new HashMap<>();
                    params.put("metricsEnabled", true);
                    HttpEntity<Map<String, Boolean>> entity = new HttpEntity<>(params, headers);
                    sslTemplate.put(outlineApi.enableMetrics(), entity);
                }
            }
        } catch (Throwable t) {
            logger.error("check outline status error: {}", t.getClass().getName(), t);
        }
    }
}
