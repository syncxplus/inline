package com.testbird.inline.metrics;

import com.testbird.inline.service.MemoryUsageStatus;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class MemoryGauge {
    private final Gauge gauge;

    public MemoryGauge(@Autowired CollectorRegistry collectorRegistry) {
        gauge = Gauge.build()
                .name("server_memory_usage")
                .help("Server memory usage")
                .register(collectorRegistry);
        gauge.set(MemoryUsageStatus.getMemoryUsage());
    }

    public Gauge get() {
        return gauge;
    }
}
