package com.epam.crm.config;

import com.epam.crm.model.*;
import com.epam.crm.model.rowmapper.TraineeRowMapper;
import com.epam.crm.model.rowmapper.TrainerRowMapper;
import com.epam.crm.model.rowmapper.TrainingRowMapper;
import com.epam.crm.model.rowmapper.TrainingTypeRowMapper;
import com.epam.crm.util.FileStorageParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.nio.file.Paths;
import java.util.HashMap;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class StorageConfig {

    private final TraineeRowMapper traineeRowMapper;
    private final TrainerRowMapper trainerRowMapper;
    private final TrainingRowMapper trainingRowMapper;
    private final TrainingTypeRowMapper trainingTypeRowMapper;

    @Value("${trainees.path:#{null}}")
    private String TRAINEES_PATH;
    @Value("${trainers.path:#{null}}")
    private String TRAINERS_PATH;
    @Value("${trainings.path:#{null}}")
    private String TRAININGS_PATH;
    @Value("${training.types.path:#{null}}")
    private String TRAINING_TYPES_PATH;

    private final HashMap<Long, Trainee> trainees = new HashMap<>();
    private final HashMap<Long, Trainer> trainers = new HashMap<>();
    private final HashMap<Long, Training> trainings = new HashMap<>();
    private final HashMap<Long, TrainingType> trainingTypes = new HashMap<>();
    private final HashMap<String, User> users = new HashMap<>();

    @Bean("trainees")
    public HashMap<Long, Trainee> trainees() {
        return trainees;
    }

    @Bean("trainers")
    public HashMap<Long, Trainer> trainers() {
        return trainers;
    }

    @Bean("trainings")
    public HashMap<Long, Training> trainings() {
        return trainings;
    }

    @Bean("trainingTypes")
    public HashMap<Long, TrainingType> trainingTypes() {
        return trainingTypes;
    }

    @Bean("users")
    public HashMap<String, User> users() {
        return users;
    }

    @PostConstruct
    void initStorage() {
        log.info("Initializing in-memory storages from files (if paths are provided)");

        if (TRAINEES_PATH != null) {
            try {
                log.info("Parsing trainees from: {}", TRAINEES_PATH);
                FileStorageParser.parseFile(Paths.get(TRAINEES_PATH), trainees, traineeRowMapper);
                log.info("Trainees loaded: {}", trainees.size());
            } catch (Exception e) {
                log.error("Failed to parse trainees from {}: {}", TRAINEES_PATH, e.getMessage(), e);
            }
        }

        if (TRAINERS_PATH != null) {
            try {
                log.info("Parsing trainers from: {}", TRAINERS_PATH);
                FileStorageParser.parseFile(Paths.get(TRAINERS_PATH), trainers, trainerRowMapper);
                log.info("Trainers loaded: {}", trainers.size());
            } catch (Exception e) {
                log.error("Failed to parse trainers from {}: {}", TRAINERS_PATH, e.getMessage(), e);
            }
        }

        if (TRAININGS_PATH != null) {
            try {
                log.info("Parsing trainings from: {}", TRAININGS_PATH);
                FileStorageParser.parseFile(Paths.get(TRAININGS_PATH), trainings, trainingRowMapper);
                log.info("Trainings loaded: {}", trainings.size());
            } catch (Exception e) {
                log.error("Failed to parse trainings from {}: {}", TRAININGS_PATH, e.getMessage(), e);
            }
        }

        if (TRAINING_TYPES_PATH != null) {
            try {
                log.info("Parsing training types from: {}", TRAINING_TYPES_PATH);
                FileStorageParser.parseFile(Paths.get(TRAINING_TYPES_PATH), trainingTypes, trainingTypeRowMapper);
                log.info("Training types loaded: {}", trainingTypes.size());
            } catch (Exception e) {
                log.error("Failed to parse training types from {}: {}", TRAINING_TYPES_PATH, e.getMessage(), e);
            }
        }

        users.clear();
        trainees.forEach((id, t) -> users.put(t.getUsername(), t));
        trainers.forEach((id, t) -> users.put(t.getUsername(), t));
        log.info("Users assembled from trainees+trainers: {}", users.size());
    }
}
