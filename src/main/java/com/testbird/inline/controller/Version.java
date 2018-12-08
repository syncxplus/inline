package com.testbird.inline.controller;

import com.testbird.inline.metrics.VersionGauge;
import com.testbird.inline.util.OutlineApi;
import io.prometheus.client.exporter.common.TextFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/version")
@WebEndpoint(id = "version")
public class Version {
    private final static Logger logger = LoggerFactory.getLogger(Version.class);
    private final OutlineApi outlineApi;
    private final RestTemplate sslTemplate;
    private final VersionGauge gauge;
    private static final long CACHE_TIME = 300000;
    private static long expiredTime = System.currentTimeMillis();
    private static String version;

    public Version(@Autowired OutlineApi outlineApi, @Autowired RestTemplate sslTemplate, @Autowired VersionGauge gauge, @Value("${version}") String version) {
        this.outlineApi = outlineApi;
        this.sslTemplate = sslTemplate;
        this.gauge = gauge;
        Version.version = version;
        gauge.get().labels(VersionGauge.VERSION_LABEL_WRAPPER).set(VersionGauge.parseVersion(version));
    }

    @ReadOperation
    @RequestMapping("")
    public Map version() {
        Map<String, String> map = new HashMap<>();
        map.put(VersionGauge.VERSION_LABEL_WRAPPER, version);
        map.put(VersionGauge.VERSION_LABEL_SHADOWBOX, getShadowboxVersion());
        return ApiResponse.successfulResponse().setData(map).generate();
    }

    @Cacheable(cacheNames = "version", condition = "!#root.target.isExpired()")
    @RequestMapping(value = "/", produces = TextFormat.CONTENT_TYPE_004)
    public String metrics() throws IOException {
        Writer writer = new StringWriter();
        TextFormat.write004(writer, Collections.enumeration(gauge.get().collect()));
        return writer.toString();
    }

    public boolean isExpired() {
        if (expiredTime < System.currentTimeMillis()) {
            expiredTime = System.currentTimeMillis() + CACHE_TIME;
            return true;
        } else {
            return false;
        }
    }

    private String getShadowboxVersion() {
        String v = "unknown";
        try {
            Map map = sslTemplate.getForObject(outlineApi.info(), Map.class);
            if (map != null && map.get("version") != null) {
                v = String.valueOf(map.get("version"));
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
        return v;
    }
}
