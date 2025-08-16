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

import java.nio.file.Paths;
import java.util.HashMap;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class StorageConfig {

    @Value("${trainees.path:#{null}}")
    private String TRAINEES_PATH;
    @Value("${trainers.path:#{null}}")
    private String TRAINERS_PATH;
    @Value("${trainings.path:#{null}}")
    private String TRAININGS_PATH;
    @Value("${training.types.path:#{null}}")
    private String TRAINING_TYPES_PATH;

    @Bean("users")
    public HashMap<String, User> users(HashMap<Long, Trainee> trainees, HashMap<Long, Trainer> trainers) {
        HashMap<String, User> users = new HashMap<>();
        log.info("Users storage bean initialized");

        if (!trainees.isEmpty()) {
            log.debug("Found existing trainees. Adding trainees to users.");
            trainees.forEach((id, trainee) -> users.put(trainee.getUsername(), trainee));
        }

        if (!trainers.isEmpty()) {
            log.debug("Found existing trainers. Adding trainers to users.");
            trainers.forEach((id, trainer) -> users.put(trainer.getUsername(), trainer));
        }

        return users;
    }

    @Bean("trainees")
    public HashMap<Long, Trainee> trainees(TraineeRowMapper rowMapper) {
        HashMap<Long, Trainee> trainees = new HashMap<>();
        log.info("Trainees storage bean initialized");

        if (TRAINEES_PATH != null) {
            log.info("Trainees path defined: {}", TRAINEES_PATH);
            FileStorageParser.parseFile(Paths.get(TRAINEES_PATH), trainees, rowMapper);
        }

        return trainees;
    }

    @Bean("trainers")
    public HashMap<Long, Trainer> trainers(TrainerRowMapper rowMapper) {
        HashMap<Long, Trainer> trainers = new HashMap<>();
        log.info("Trainers storage bean initialized");

        if (TRAINERS_PATH != null) {
            log.info("Trainers path defined: {}", TRAINERS_PATH);
            FileStorageParser.parseFile(Paths.get(TRAINERS_PATH), trainers, rowMapper);
        }

        return trainers;
    }

    @Bean("trainings")
    public HashMap<Long, Training> trainings(TrainingRowMapper rowMapper) {
        HashMap<Long, Training> trainings = new HashMap<>();
        log.info("Trainings storage bean initialized");

        if (TRAININGS_PATH != null) {
            log.info("Trainings path defined: {}", TRAININGS_PATH);
            FileStorageParser.parseFile(Paths.get(TRAININGS_PATH), trainings, rowMapper);
        }

        return trainings;
    }

    @Bean("trainingTypes")
    public HashMap<Long, TrainingType> trainingTypes(TrainingTypeRowMapper rowMapper) {
        HashMap<Long, TrainingType> trainingTypes = new HashMap<>();
        log.info("Training Types storage bean initialized");

        if (TRAINING_TYPES_PATH != null) {
            log.info("Training types path defined: {}", TRAINING_TYPES_PATH);
            FileStorageParser.parseFile(Paths.get(TRAINING_TYPES_PATH), trainingTypes, rowMapper);
        }

        return trainingTypes;
    }

}
