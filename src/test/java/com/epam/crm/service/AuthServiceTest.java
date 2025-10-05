package com.epam.crm.service;

import com.epam.crm.model.User;
import com.epam.crm.repository.UserRepository;
import com.epam.crm.security.JwtService;
import com.epam.crm.security.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    AuthenticationManager authenticationManager;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;
    LoginAttemptService loginAttemptService;

    AuthService authService;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        loginAttemptService = mock(LoginAttemptService.class);

        authService = new AuthService(authenticationManager, userRepository, passwordEncoder, jwtService, loginAttemptService);
    }

    @Test
    void authenticateAndIssueToken_success() {
        String username = " user ";
        String raw = "pass";

        Authentication auth = new UsernamePasswordAuthenticationToken(
                username.trim(), null, Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(loginAttemptService.isBlocked(username)).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtService.createToken(username.trim(), "ROLE_USER")).thenReturn("JWT");

        String token = authService.authenticateAndIssueToken(username, raw);

        assertEquals("JWT", token);
        verify(loginAttemptService).loginSucceeded(username);
        verify(jwtService).createToken(username.trim(), "ROLE_USER");
    }

    @Test
    void authenticateAndIssueToken_blocked_throws() {
        when(loginAttemptService.isBlocked("alex")).thenReturn(true);

        assertThrows(BadCredentialsException.class,
                () -> authService.authenticateAndIssueToken("alex", "pwd"));

        verify(authenticationManager, never()).authenticate(any());
        verify(loginAttemptService, never()).loginSucceeded(any());
    }

    @Test
    void authenticateAndIssueToken_badCredentials_propagates_and_marksFail() {
        when(loginAttemptService.isBlocked("mike")).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        assertThrows(BadCredentialsException.class,
                () -> authService.authenticateAndIssueToken("mike", "wrong"));

        verify(loginAttemptService).loginFailed("mike");
        verify(jwtService, never()).createToken(any(), any());
    }

    @Test
    void changePassword_success() {
        var user = new User();
        user.setUsername("u");
        user.setPassword("ENC_OLD");

        when(userRepository.findByUsername("u")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "ENC_OLD")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("ENC_NEW");

        authService.changePassword(" u ", "old", "new");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("ENC_NEW", captor.getValue().getPassword());
    }

    @Test
    void changePassword_userNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> authService.changePassword("ghost", "a", "b"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_wrongOldPassword() {
        var user = new User();
        user.setUsername("u");
        user.setPassword("ENC_OLD");

        when(userRepository.findByUsername("u")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "ENC_OLD")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> authService.changePassword("u", "old", "new"));

        verify(userRepository, never()).save(any());
    }
}
