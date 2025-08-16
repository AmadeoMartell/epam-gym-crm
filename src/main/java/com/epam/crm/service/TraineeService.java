package com.epam.crm.service;

import com.epam.crm.dao.TraineeDao;
import com.epam.crm.model.Trainee;
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
public class TraineeService {
    private final TraineeDao dao;
    private final HashMap<String, User> users;

    public Trainee createProfile(String firstName, String lastName, Date dob, String address) {
        String username = UniqueUsernameGenerator.generate(users.keySet(), firstName, lastName);
        String password = PasswordGenerator.generate(10);
        Trainee t = Trainee.builder()
                .id(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password(password)
                .active(true)
                .build();
        return dao.create(t);
    }

    public Trainee update(Trainee t) {
        return dao.update(t);
    }

    public boolean delete(Long id) {
        return dao.delete(id);
    }

    public Optional<Trainee> get(Long id) {
        return dao.findById(id);
    }

    public List<Trainee> list() {
        return dao.findAll();
    }
}
