package com.epam.crm.service;

import com.epam.crm.model.Trainee;
import com.epam.crm.model.Trainer;
import com.epam.crm.model.Training;
import com.epam.crm.model.User;
import com.epam.crm.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GymFacade {

    private final UserRepository userRepository;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    private User requireAuth(String username, String password) {
        User u = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User not found: " + username));
        if (!Objects.equals(u.getPassword(), password)) {
            throw new SecurityException("Invalid credentials");
        }
        return u;
    }

    public TraineeService.CreatedAccount createTraineeProfile(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        return traineeService.createTrainee(firstName, lastName, dateOfBirth, address);
    }

    public TrainerService.CreatedAccount createTrainerProfile(String firstName, String lastName, String specialization) {
        return trainerService.createTrainer(firstName, lastName, specialization);
    }

    public boolean matchCredentials(String username, String password) {
        try {
            requireAuth(username, password);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public Trainee getTraineeByUsername(String authUser, String authPass, String traineeUsername) {
        requireAuth(authUser, authPass);
        return traineeService.findByUsername(traineeUsername).orElseThrow(() -> new NoSuchElementException("Trainee not found: " + traineeUsername));
    }

    public Trainer getTrainerByUsername(String authUser, String authPass, String trainerUsername) {
        requireAuth(authUser, authPass);
        return trainerService.findByUsernameOrThrow(trainerUsername);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        traineeService.changePassword(username, oldPassword, newPassword);
    }

    public Trainee updateTraineeProfile(String authUser, String authPass, String username, String firstName, String lastName, LocalDate dateOfBirth, String address) {
        requireAuth(authUser, authPass);
        return traineeService.updateProfile(username, firstName, lastName, dateOfBirth, address);
    }

    public Trainer updateTrainerProfile(String authUser, String authPass, String username, String firstName, String lastName, String specialization) {
        requireAuth(authUser, authPass);
        return trainerService.updateProfile(username, firstName, lastName, specialization);
    }

    public void activate(String authUser, String authPass, String targetUsername) {
        requireAuth(authUser, authPass);
        traineeService.activate(targetUsername);
    }

    public void deactivate(String authUser, String authPass, String targetUsername) {
        requireAuth(authUser, authPass);
        traineeService.deactivate(targetUsername);
    }


    public void deleteTrainee(String authUser, String authPass, String traineeUsername) {
        requireAuth(authUser, authPass);
        traineeService.deleteByUsername(traineeUsername);
    }


    public Training addTraining(String authUser, String authPass, String traineeUsername, String trainerUsername, String trainingTypeName, String name, LocalDate date, int duration) {
        requireAuth(authUser, authPass);
        return trainingService.addTraining(traineeUsername, trainerUsername, trainingTypeName, name, date, duration);
    }


    public List<Training> listTrainingsForTrainee(String authUser, String authPass, String traineeUsername, LocalDate from, LocalDate to, String trainerName, String typeName) {
        requireAuth(authUser, authPass);
        return trainingService.findForTraineeWithCriteria(traineeUsername, from, to, trainerName, typeName);
    }

    public List<Training> listTrainingsForTrainer(String authUser, String authPass, String trainerUsername, LocalDate from, LocalDate to, String traineeName) {
        requireAuth(authUser, authPass);
        return trainingService.findForTrainerWithCriteria(trainerUsername, from, to, traineeName);
    }


    public List<Trainer> getUnassignedTrainers(String authUser, String authPass, String traineeUsername) {
        requireAuth(authUser, authPass);
        return traineeService.getUnassignedTrainersForTrainee(traineeUsername);
    }

    @Transactional
    public void updateTraineeTrainers(String authUser, String authPass, String traineeUsername, List<String> trainerUsernames) {
        requireAuth(authUser, authPass);
        traineeService.updateTraineeTrainers(traineeUsername, trainerUsernames);
    }
}
