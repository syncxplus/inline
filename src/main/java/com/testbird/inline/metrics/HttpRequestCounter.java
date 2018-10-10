package com.testbird.inline.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class HttpRequestCounter {
    private final Counter counter;

    public HttpRequestCounter(@Autowired CollectorRegistry collectorRegistry) {
        counter = Counter.build()
                .name("server_request_count")
                .help("Total request count")
                .labelNames("method", "uri")
                .register(collectorRegistry);
    }

    public Counter get() {
        return counter;
    }
}
