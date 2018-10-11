package com.testbird.inline.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class OutlineGauge {
    private final Gauge gauge;

    public OutlineGauge(@Autowired CollectorRegistry collectorRegistry) {
        gauge = Gauge.build()
                .name("server_shadowbox_status")
                .help("Shadowbox status: 1(OK) 0(NOK)")
                .register(collectorRegistry);
        gauge.set(1d);
    }

    public Gauge get() {
        return gauge;
    }
}
