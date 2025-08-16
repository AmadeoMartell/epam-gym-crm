package com.epam.crm.dao;

import com.epam.crm.model.Trainer;
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
public class TrainerDao {
    @Qualifier("trainers")
    private final Map<Long, Trainer> storage;

    @Autowired
    public TrainerDao(Map<Long, Trainer> storage) {
        this.storage = storage;
    }

    public Trainer create(Trainer t) {
        storage.put(t.getId(), t);
        log.info("Trainer created: {}", t);
        return t;
    }

    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Trainer> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Trainer update(Trainer t) {
        storage.put(t.getId(), t);
        log.info("Trainer updated: {}", t);
        return t;
    }
}
