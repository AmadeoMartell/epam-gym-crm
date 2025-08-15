package com.epam.crm.config;

import com.epam.crm.model.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    @Value("${users.path:#{null}}")
    private String USERS_PATH;


    @Bean("users")
    public HashMap<String, User> users() {
        return new HashMap<>();
    }

    @Bean("trainees")
    public HashMap<Long, Trainee> trainees() {
        HashMap<Long, Trainee> trainees = new HashMap<>();
        log.info("Trainees storage bean initialized");

        return trainees;
    }

    @Bean("trainers")
    public HashMap<Long, Trainer> trainers() {
        log.info("Trainers storage bean initialized");
        return new HashMap<>();
    }

    @Bean("trainings")
    public HashMap<Long, Training> trainings() {
        log.info("Trainings storage bean initialized");
        return new HashMap<>();
    }
    @Bean("trainingTypes")
    public HashMap<Long, TrainingType> trainingTypes() {
        log.info("Training Types storage bean initialized");
        return new HashMap<>();
    }

}
