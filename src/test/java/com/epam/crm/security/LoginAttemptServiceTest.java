package com.epam.crm.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceTest {

    @Test
    void blocks_after_three_failed_attempts_and_resets_on_success() {
        LoginAttemptService s = new LoginAttemptService();

        String u = "user";

        assertFalse(s.isBlocked(u));

        s.loginFailed(u);
        assertFalse(s.isBlocked(u));

        s.loginFailed(u);
        assertFalse(s.isBlocked(u));

        s.loginFailed(u);
        assertTrue(s.isBlocked(u));

        s.loginSucceeded(u);
        assertFalse(s.isBlocked(u));
    }
}
