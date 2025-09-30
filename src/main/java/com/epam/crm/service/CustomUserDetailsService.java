package com.epam.crm.service;

import com.epam.crm.model.User;
import com.epam.crm.repository.TraineeRepository;
import com.epam.crm.repository.TrainerRepository;
import com.epam.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User is deactivated: " + username);
        }

        String role;

        if (trainerRepository.findByUsername(username).isPresent()){
            role = "ROLE_TRAINER";
        } else if (traineeRepository.findByUsername(username).isPresent()){
            role = "ROLE_TRAINEE";
        }  else {
            throw new UsernameNotFoundException("User ROLE is not defined: " + username);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
                .build();
    }
}
