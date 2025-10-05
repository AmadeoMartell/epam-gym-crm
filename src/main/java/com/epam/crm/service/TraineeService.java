package com.epam.crm.service;

import com.epam.crm.model.Trainee;
import com.epam.crm.model.Trainer;
import com.epam.crm.model.User;
import com.epam.crm.repository.TraineeRepository;
import com.epam.crm.repository.TrainerRepository;
import com.epam.crm.repository.TrainingRepository;
import com.epam.crm.repository.UserRepository;
import com.epam.crm.util.PasswordGenerator;
import com.epam.crm.util.UniqueUsernameGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final PasswordEncoder passwordEncoder;

    @Value
    public static class CreatedAccount {
        Long userId;
        String username;
        String password;
    }

    @Transactional
    public CreatedAccount createTrainee(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        boolean existsAsTrainer = trainerRepository.findAll().stream()
                .anyMatch(tr -> tr.getFirstName().equalsIgnoreCase(firstName)
                        && tr.getLastName().equalsIgnoreCase(lastName));
        if (existsAsTrainer) {
            throw new IllegalStateException("Person is already registered as Trainer");
        }

        Set<String> taken = userRepository.findAll().stream().map(User::getUsername).collect(Collectors.toSet());
        String username = UniqueUsernameGenerator.generate(taken, firstName, lastName);

        String rawPassword = PasswordGenerator.generate(10);

        Trainee t = new Trainee();
        t.setFirstName(firstName);
        t.setLastName(lastName);
        t.setUsername(username);
        t.setPassword(passwordEncoder.encode(rawPassword));
        t.setIsActive(true);
        t.setDateOfBirth(dateOfBirth);
        t.setAddress(address);

        Trainee saved = traineeRepository.save(t);
        log.info("Trainee created: id={}, username={}", saved.getId(), saved.getUsername());
        return new CreatedAccount(saved.getId(), username, rawPassword);
    }

    @Transactional
    public Optional<Trainee> findByUsername(String username) {
        return traineeRepository.findByUsername(username);
    }

    public Optional<Trainee> findById(Long id) {
        return traineeRepository.findById(id);
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
    public Trainee updateProfile(String username, String firstName, String lastName, LocalDate dateOfBirth, String address) {
        Trainee t = traineeRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("Trainee not found: " + username));

        if (firstName != null) t.setFirstName(firstName);
        if (lastName != null) t.setLastName(lastName);
        t.setDateOfBirth(dateOfBirth);
        t.setAddress(address);

        Trainee saved = traineeRepository.save(t);
        log.info("Trainee updated: {}", saved.getUsername());
        return saved;
    }

    @Transactional
    public void activate(String username) {
        User u = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User not found: " + username));
        if (Boolean.TRUE.equals(u.getIsActive())) {
            throw new IllegalStateException("User already active");
        }
        u.setIsActive(true);
        userRepository.save(u);
        log.info("User activated: {}", username);
    }

    @Transactional
    public void deactivate(String username) {
        User u = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User not found: " + username));
        if (Boolean.FALSE.equals(u.getIsActive())) {
            throw new IllegalStateException("User already deactivated");
        }
        u.setIsActive(false);
        userRepository.save(u);
        log.info("User deactivated: {}", username);
    }

    @Transactional
    public void deleteByUsername(String username) {
        trainingRepository.deleteByTraineeUsername(username);

        Trainee t = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Trainee not found: " + username));

        traineeRepository.delete(t);
        log.info("Trainee deleted: {} (trainings cascade deleted)", username);
    }

    @Transactional
    public List<Trainer> getUnassignedTrainersForTrainee(String traineeUsername) {
        Trainee trainee = traineeRepository.findWithTrainersByUsername(traineeUsername).orElseThrow(() -> new NoSuchElementException("Trainee not found: " + traineeUsername));

        Set<Long> assignedIds = trainee.getTrainers().stream().map(Trainer::getId).collect(Collectors.toSet());

        return trainerRepository.findAll().stream().filter(tr -> !assignedIds.contains(tr.getId())).collect(Collectors.toList());
    }

    @Transactional
    public void updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        Trainee trainee = traineeRepository.findWithTrainersByUsername(traineeUsername).orElseThrow(() -> new NoSuchElementException("Trainee not found: " + traineeUsername));

        Set<Trainer> newSet = trainerUsernames == null ? Collections.emptySet() : trainerUsernames.stream().map(u -> trainerRepository.findByUsername(u).orElseThrow(() -> new NoSuchElementException("Trainer not found: " + u))).collect(Collectors.toSet());

        Set<Trainer> toRemove = new HashSet<>(trainee.getTrainers());
        toRemove.removeAll(newSet);
        Set<Trainer> toAdd = new HashSet<>(newSet);
        toAdd.removeAll(trainee.getTrainers());

        toRemove.forEach(trainee::removeTrainer);
        toAdd.forEach(trainee::addTrainer);

        traineeRepository.save(trainee);
        log.info("Trainee {} trainers updated. Now assigned: {}", traineeUsername, trainee.getTrainers().stream().map(User::getUsername).toList());
    }
}
