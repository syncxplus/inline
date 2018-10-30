package com.testbird.inline.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class PortGauge {
    private final Gauge gauge;

    public PortGauge(@Autowired CollectorRegistry collectorRegistry) {
        gauge = Gauge.build()
                .name("server_port_count")
                .help("Count the port with data transfer")
                .register(collectorRegistry);
    }

    public Gauge get() {
        return gauge;
    }
}
