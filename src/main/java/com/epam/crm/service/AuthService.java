package com.epam.crm.service;

import com.epam.crm.model.User;
import com.epam.crm.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public void authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));
        if (!Objects.equals(user.getPassword(), password)) {
            throw new SecurityException("Invalid credentials");
        }
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));
        if (!Objects.equals(user.getPassword(), oldPassword)) {
            throw new SecurityException("Invalid old password");
        }
        user.setPassword(newPassword);
        userRepository.save(user);
    }
}
