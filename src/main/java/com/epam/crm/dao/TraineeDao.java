package com.epam.crm.dao;

import com.epam.crm.model.Trainee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class TraineeDao {
    @Qualifier("trainees")
    private final Map<Long, Trainee> storage;

    @Autowired
    public TraineeDao(Map<Long, Trainee> storage) {
        this.storage = storage;
    }

    public Trainee create(Trainee t) {
        storage.put(t.getId(), t);
        log.info("Trainee created: {}", t);
        return t;
    }

    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Trainee> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Trainee update(Trainee t) {
        storage.put(t.getId(), t);
        log.info("Trainee updated: {}", t);
        return t;
    }

    public boolean delete(Long id) {
        Trainee r = storage.remove(id);
        log.info("Trainee deleted: {}", id);
        return r != null;
    }
}
