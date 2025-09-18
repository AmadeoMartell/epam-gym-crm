package com.epam.crm.service;

import com.epam.crm.model.User;
import com.epam.crm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @InjectMocks AuthService authService;

    @Test
    void authenticate_ok() {
        User u = new User();
        u.setUsername("john");
        u.setPassword("pass");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(u));

        assertDoesNotThrow(() -> authService.authenticate("john", "pass"));
    }

    @Test
    void authenticate_userNotFound() {
        when(userRepository.findByUsername("x")).thenReturn(Optional.empty());
        assertThrows(java.util.NoSuchElementException.class,
                () -> authService.authenticate("x", "any"));
    }

    @Test
    void authenticate_invalidPassword() {
        User u = new User();
        u.setUsername("john");
        u.setPassword("pass");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(u));

        assertThrows(SecurityException.class,
                () -> authService.authenticate("john", "wrong"));
    }

    @Test
    void changePassword_ok() {
        User u = new User();
        u.setUsername("john");
        u.setPassword("old");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(u));

        authService.changePassword("john", "old", "new");
        verify(userRepository).save(argThat(saved -> "new".equals(saved.getPassword())));
    }

    @Test
    void changePassword_invalidOld() {
        User u = new User();
        u.setUsername("john");
        u.setPassword("old");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(u));

        assertThrows(SecurityException.class,
                () -> authService.changePassword("john", "BAD", "new"));
    }
}
