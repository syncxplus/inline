package com.testbird.inline.controller;

import com.testbird.inline.metrics.VersionGauge;
import com.testbird.inline.util.OutlineApi;
import io.prometheus.client.exporter.common.TextFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/version")
@WebEndpoint(id = "version")
public class Version {
    private final static Logger logger = LoggerFactory.getLogger(Version.class);
    private final static String version;
    private final OutlineApi outlineApi;
    private final RestTemplate sslTemplate;
    private final VersionGauge gauge;

    static {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Version.class.getResourceAsStream("/version")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        version = sb.toString();
    }

    public Version(@Autowired OutlineApi outlineApi, @Autowired RestTemplate sslTemplate, @Autowired VersionGauge versionGauge) {
        this.outlineApi = outlineApi;
        this.sslTemplate = sslTemplate;
        gauge = versionGauge;
        gauge.get().labels(VersionGauge.VERSION_LABEL_WRAPPER).set(VersionGauge.parseVersion(version));
    }

    @ReadOperation
    private Map version() {
        Map<String, String> map = new HashMap<>();
        map.put(VersionGauge.VERSION_LABEL_WRAPPER, version);
        map.put(VersionGauge.VERSION_LABEL_SHADOWBOX, getShadowboxVersion());
        return ApiResponse.successfulResponse().setData(map).generate();
    }

    @RequestMapping(value = {"", "/"}, produces = TextFormat.CONTENT_TYPE_004)
    private String metrics() throws IOException {
        Writer writer = new StringWriter();
        TextFormat.write004(writer, Collections.enumeration(gauge.get().collect()));
        return writer.toString();
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
