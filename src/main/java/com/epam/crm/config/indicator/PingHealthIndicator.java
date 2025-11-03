package com.epam.crm.config.indicator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("pingCustom")
class PingHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        return Health.up().withDetail("pong", true).build();
    }
}
