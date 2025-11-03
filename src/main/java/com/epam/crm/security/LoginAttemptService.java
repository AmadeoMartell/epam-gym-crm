package com.epam.crm.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_SECONDS = 5 * 60;

    private static class Stat {
        int fails = 0;
        Instant blockedUntil = null;
    }

    private final Map<String, Stat> stats = new ConcurrentHashMap<>();

    public void loginFailed(String username) {
        var s = stats.computeIfAbsent(username, u -> new Stat());
        s.fails++;
        if (s.fails >= MAX_ATTEMPTS) {
            s.blockedUntil = Instant.now().plusSeconds(BLOCK_SECONDS);
        }
    }

    public void loginSucceeded(String username) {
        stats.remove(username);
    }

    public boolean isBlocked(String username) {
        var s = stats.get(username);
        if (s == null || s.blockedUntil == null) return false;
        if (Instant.now().isAfter(s.blockedUntil)) { stats.remove(username); return false; }
        return true;
    }
}
