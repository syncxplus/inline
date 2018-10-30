package com.testbird.inline.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class NetworkGauge {
    private final Gauge gauge;

    public NetworkGauge(@Autowired CollectorRegistry collectorRegistry) {
        gauge = Gauge.build()
                .name("server_network_stat")
                .help("Server network stat")
                .labelNames("direction")
                .register(collectorRegistry);
    }

    public Gauge get() {
        return gauge;
    }
}
