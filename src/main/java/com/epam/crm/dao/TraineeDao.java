package com.epam.crm.dao;

import com.epam.crm.model.Trainee;
import com.epam.crm.model.User;
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
    @Qualifier("users")
    private final Map<String, User> users;

    @Autowired
    public TraineeDao(Map<Long, Trainee> storage, Map<String, User> users) {
        this.storage = storage;
        this.users = users;
    }

    public Trainee create(Trainee t) {
        storage.put(t.getId(), t);
        users.put(t.getUsername(), t);
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
        users.put(t.getUsername(), t);
        log.info("Trainee updated: {}", t);
        return t;
    }

    public boolean delete(Long id) {
        Trainee r = storage.remove(id);
        if (r != null) {
            users.remove(r.getUsername());
        }
        log.info("Trainee deleted: {}", id);
        return r != null;
    }
}
