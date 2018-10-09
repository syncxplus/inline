package com.testbird.inline.controller;

import com.testbird.inline.util.OutlineApi;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;
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
    private final static String version;
    private final OutlineApi outlineApi;
    private final RestTemplate sslTemplate;
    private final Gauge gauge;

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

    public Version(@Autowired OutlineApi outlineApi, @Autowired RestTemplate sslTemplate, @Autowired CollectorRegistry collectorRegistry) {
        this.outlineApi = outlineApi;
        this.sslTemplate = sslTemplate;
        gauge = Gauge.build().name("app_version").help("server version number").register(collectorRegistry);
        gauge.set(Double.parseDouble(version));
    }

    @ReadOperation
    private Map version() {
        Map<String, String> map = new HashMap<>();
        map.put("inline", version);
        map.put("shadowbox", String.valueOf(sslTemplate.getForObject(outlineApi.version(), Map.class).get("version")));
        return ApiResponse.successfulResponse().setData(map).generate();
    }

    @RequestMapping(value = {"", "/"}, produces = TextFormat.CONTENT_TYPE_004)
    private String standalone() throws IOException {
        Writer writer = new StringWriter();
        TextFormat.write004(writer, Collections.enumeration(gauge.collect()));
        return writer.toString();
    }
}
