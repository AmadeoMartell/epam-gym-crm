package com.epam.crm.service;

import com.epam.crm.dao.TrainerDao;
import com.epam.crm.model.Trainer;
import com.epam.crm.model.User;
import com.epam.crm.util.PasswordGenerator;
import com.epam.crm.util.UniqueUsernameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerService {
    private final TrainerDao dao;
    private final HashMap<String, User> users;

    public Trainer createProfile(String firstName, String lastName, Long specializationId) {
        String username = UniqueUsernameGenerator.generate(users.keySet(), firstName, lastName);
        String password = PasswordGenerator.generate(10);
        Trainer t = Trainer.builder()
                .id(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password(password)
                .active(true)
                .specializationId(specializationId)
                .build();
        return dao.create(t);
    }

    public Trainer update(Trainer t) {
        return dao.update(t);
    }

    public Optional<Trainer> get(Long id) {
        return dao.findById(id);
    }

    public List<Trainer> list() {
        return dao.findAll();
    }
}
