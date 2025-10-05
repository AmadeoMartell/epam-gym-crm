package com.epam.crm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Test
    void create_and_parse_token_roundtrip() {
        String secret = "this_is_a_very_long_test_secret_key_32b_min!";
        long ttlSeconds = 3600;

        JwtService jwt = new JwtService(secret, ttlSeconds);

        String token = jwt.createToken("alice", "ROLE_ADMIN");
        assertNotNull(token);

        Jws<Claims> parsed = jwt.parse(token);
        assertEquals("alice", parsed.getBody().getSubject());
        assertEquals("ROLE_ADMIN", parsed.getBody().get("role", String.class));
        assertTrue(parsed.getBody().getExpiration().getTime() > System.currentTimeMillis());
    }
}
