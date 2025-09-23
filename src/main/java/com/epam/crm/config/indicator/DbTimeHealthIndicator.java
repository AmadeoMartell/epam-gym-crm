package com.epam.crm.config.indicator;

import org.springframework.boot.actuate.health.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

@Component("dbTime")
public class DbTimeHealthIndicator implements HealthIndicator {
    private final JdbcTemplate jdbc;

    public DbTimeHealthIndicator(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Override public Health health() {
        try {
            Timestamp ts = jdbc.queryForObject("select now()", Timestamp.class);
            if (ts == null) return Health.down().withDetail("error","select now() returned null").build();
            Instant dbNow = ts.toInstant(), appNow = Instant.now();
            Duration diff = Duration.between(appNow, dbNow).abs();
            Health.Builder b = diff.compareTo(Duration.ofMinutes(2)) <= 0 ? Health.up() : Health.status(Status.OUT_OF_SERVICE);
            return b.withDetail("dbNow", dbNow).withDetail("appNow", appNow).withDetail("skew", diff.toSeconds()+"s").build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
