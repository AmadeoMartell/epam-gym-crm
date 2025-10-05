package com.epam.crm.service;

import com.epam.crm.model.User;
import com.epam.crm.repository.UserRepository;
import com.epam.crm.security.JwtService;
import com.epam.crm.security.LoginAttemptService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;

    public String authenticateAndIssueToken(String username, String rawPassword) {
        if (loginAttemptService.isBlocked(username)) {
            throw new BadCredentialsException("User temporarily blocked. Try again later");
        }
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username.trim(), rawPassword)
            );
            loginAttemptService.loginSucceeded(username);
            var role = auth.getAuthorities().iterator().next().getAuthority();
            return jwtService.createToken(username.trim(), role);
        } catch (BadCredentialsException ex) {
            loginAttemptService.loginFailed(username);
            throw ex;
        }
    }



    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
