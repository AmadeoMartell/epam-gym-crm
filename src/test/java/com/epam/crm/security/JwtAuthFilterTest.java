package com.epam.crm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void setsAuthentication_whenBearerIsValid() throws ServletException, IOException {
        String secret = "this_is_a_very_long_test_secret_key_32b_min!";
        JwtService realJwt = new JwtService(secret, 3600);
        String token = realJwt.createToken("bob", "ROLE_USER");

        JwtService jwtService = spy(realJwt);
        JwtAuthFilter filter = new JwtAuthFilter(jwtService);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse res = new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(req, res, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("bob", auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        verify(chain, times(1)).doFilter(req, res);
    }

    @Test
    void doesNothing_whenHeaderMissingOrInvalid() throws ServletException, IOException {
        JwtService jwtService = mock(JwtService.class);
        JwtAuthFilter filter = new JwtAuthFilter(jwtService);

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(req, res, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain, times(1)).doFilter(req, res);
        verifyNoInteractions(jwtService);
    }
}
