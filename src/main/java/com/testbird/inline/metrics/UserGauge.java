package com.testbird.inline.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class UserGauge {
    private final Gauge gauge;

    public UserGauge(@Autowired CollectorRegistry collectorRegistry) {
        gauge = Gauge.build()
                .name("server_user_total")
                .help("Active user count")
                .register(collectorRegistry);
    }

    public Gauge get() {
        return gauge;
    }
}
