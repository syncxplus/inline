package com.testbird.inline.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class UserCountryCounter {
    private final Counter counter;

    public UserCountryCounter(@Autowired CollectorRegistry collectorRegistry) {
        counter = Counter.build()
                .name("server_user_country_count")
                .help("User country count")
                .labelNames("country")
                .register(collectorRegistry);
    }

    public Counter get() {
        return counter;
    }
}
