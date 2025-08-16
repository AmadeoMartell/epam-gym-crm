package com.epam.crm.dao;

import com.epam.crm.model.Training;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class TrainingDao {
    @Qualifier("trainings")
    private final Map<Long, Training> storage;

    @Autowired
    public TrainingDao(Map<Long, Training> storage) {
        this.storage = storage;
    }

    public Training create(Training t) {
        storage.put(t.getId(), t);
        log.info("Training created: {}", t);
        return t;
    }

    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Training> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Training> findByTrainee(Long traineeId) {
        return storage.values().stream().filter(tr -> Objects.equals(tr.getTraineeId(), traineeId)).collect(Collectors.toList());
    }

    public List<Training> findByTrainer(Long trainerId) {
        return storage.values().stream().filter(tr -> Objects.equals(tr.getTrainerId(), trainerId)).collect(Collectors.toList());
    }
}
