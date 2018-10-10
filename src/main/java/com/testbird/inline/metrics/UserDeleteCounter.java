package com.testbird.inline.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class UserDeleteCounter {
    private final Counter counter;

    public UserDeleteCounter(@Autowired CollectorRegistry collectorRegistry) {
        counter = Counter.build()
                .name("http_request_user_delete")
                .help("User delete request")
                .register(collectorRegistry);
    }

    public Counter get() {
        return counter;
    }
}
