package com.epam.crm.dao;

import com.epam.crm.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingDaoTest {

    private Map<Long, Training> storage;
    private TrainingDao dao;

    @BeforeEach
    void setUp() {
        storage = new HashMap<>();
        dao = new TrainingDao(storage);
    }

    private Training training(long id, long traineeId, long trainerId, long typeId, String name) {
        return Training.builder()
                .id(id)
                .traineeId(traineeId)
                .trainerId(trainerId)
                .name(name)
                .trainingTypeId(typeId)
                .date(LocalDate.of(2025, 1, 1))
                .duration(Duration.ofMinutes(60))
                .build();
    }

    @Test
    void create_findById_and_findAll() {
        Training a = dao.create(training(1L, 10L, 100L, 7L, "Cardio"));
        Training b = dao.create(training(2L, 11L, 100L, 8L, "Strength"));

        assertThat(dao.findById(1L)).contains(a);
        assertThat(dao.findById(99L)).isEmpty();

        assertThat(dao.findAll()).containsExactlyInAnyOrder(a, b);
    }

    @Test
    void findByTrainee_filtersCorrectly() {
        Training a = dao.create(training(1L, 10L, 100L, 7L, "Cardio"));
        Training b = dao.create(training(2L, 11L, 100L, 7L, "Yoga"));
        Training c = dao.create(training(3L, 10L, 101L, 9L, "Boxing"));

        List<Training> byTrainee10 = dao.findByTrainee(10L);
        assertThat(byTrainee10).containsExactlyInAnyOrder(a, c);
    }

    @Test
    void findByTrainer_filtersCorrectly() {
        Training a = dao.create(training(1L, 10L, 100L, 7L, "Cardio"));
        Training b = dao.create(training(2L, 11L, 100L, 7L, "Yoga"));
        Training c = dao.create(training(3L, 10L, 101L, 9L, "Boxing"));

        List<Training> byTrainer100 = dao.findByTrainer(100L);
        assertThat(byTrainer100).containsExactlyInAnyOrder(a, b);
    }
}
