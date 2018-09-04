package com.testbird.inline.service;

import com.testbird.inline.util.OutlineApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        String url = outlineApi.listUsers();
        logger.info("outline status check: {}", url);
        ResponseEntity<String> response = sslTemplate.getForEntity(url, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("status code: {}", response.getStatusCode());
        } else {
            logger.info("outline server is OK");
        }
    }
}
