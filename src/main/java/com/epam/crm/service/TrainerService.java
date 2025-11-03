package com.epam.crm.service;

import com.epam.crm.model.Trainer;
import com.epam.crm.model.User;
import com.epam.crm.repository.TraineeRepository;
import com.epam.crm.repository.TrainerRepository;
import com.epam.crm.repository.UserRepository;
import com.epam.crm.util.PasswordGenerator;
import com.epam.crm.util.UniqueUsernameGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TraineeRepository traineeRepository;
    private final PasswordEncoder passwordEncoder;


    @Value
    public static class CreatedAccount {
        Long userId;
        String username;
        String password;
    }

    @Transactional
    public CreatedAccount createTrainer(String firstName, String lastName, String specialization) {
        boolean existsAsTrainee = traineeRepository.findAll().stream()
                .anyMatch(t -> t.getFirstName().equalsIgnoreCase(firstName)
                        && t.getLastName().equalsIgnoreCase(lastName));
        if (existsAsTrainee) {
            throw new IllegalStateException("Person is already registered as Trainee");
        }

        Set<String> taken = userRepository.findAll().stream().map(User::getUsername).collect(Collectors.toSet());
        String username = UniqueUsernameGenerator.generate(taken, firstName, lastName);
        String rawPassword = PasswordGenerator.generate(10);

        Trainer tr = new Trainer();
        tr.setFirstName(firstName);
        tr.setLastName(lastName);
        tr.setUsername(username);
        tr.setPassword(passwordEncoder.encode(rawPassword));
        tr.setIsActive(true);
        tr.setSpecialization(specialization);

        Trainer saved = trainerRepository.save(tr);
        log.info("Trainer created: id={}, username={}", saved.getId(), saved.getUsername());
        return new CreatedAccount(saved.getId(), username, rawPassword);
    }

    public Trainer findByUsernameOrThrow(String username) {
        return trainerRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("Trainer not found: " + username));
    }

    @Transactional
    public Trainer updateProfile(String username, String firstName, String lastName, String specialization) {
        Trainer tr = findByUsernameOrThrow(username);
        if (firstName != null) tr.setFirstName(firstName);
        if (lastName != null) tr.setLastName(lastName);
        tr.setSpecialization(specialization);
        Trainer saved = trainerRepository.save(tr);
        log.info("Trainer updated: {}", username);
        return saved;
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        User u = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User not found: " + username));
        if (!Objects.equals(u.getPassword(), oldPassword)) {
            throw new SecurityException("Invalid old password");
        }
        u.setPassword(newPassword);
        userRepository.save(u);
        log.info("Password changed for {}", username);
    }

    @Transactional
    public void activate(String username) {
        User u = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User not found: " + username));
        if (Boolean.TRUE.equals(u.getIsActive())) throw new IllegalStateException("User already active");
        u.setIsActive(true);
        userRepository.save(u);
    }

    @Transactional
    public void deactivate(String username) {
        User u = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User not found: " + username));
        if (Boolean.FALSE.equals(u.getIsActive())) throw new IllegalStateException("User already deactivated");
        u.setIsActive(false);
        userRepository.save(u);
    }
}
