package com.epam.crm.dao;

import com.epam.crm.model.Trainer;
import com.epam.crm.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class TrainerDaoTest {

    private Map<Long, Trainer> storage;
    private Map<String, User> users;
    private TrainerDao dao;

    @BeforeEach
    void setUp() {
        storage = new HashMap<>();
        users = new HashMap<>();
        dao = new TrainerDao(storage, users);
    }

    private Trainer trainer(long id, String uname) {
        return Trainer.builder()
                .id(id)
                .firstName("Alice")
                .lastName("Smith")
                .username(uname)
                .password("pwd")
                .active(true)
                .specializationId(101L)
                .build();
    }

    @Test
    void create_and_findById_and_findAll() {
        Trainer t1 = dao.create(trainer(1L, "alice"));
        Trainer t2 = dao.create(trainer(2L, "bob"));

        assertThat(dao.findById(1L)).contains(t1);
        assertThat(dao.findById(999L)).isEmpty();

        assertThat(dao.findAll()).containsExactlyInAnyOrder(t1, t2);
    }

    @Test
    void update_updatesStorageAndUsers() {
        Trainer t = dao.create(trainer(1L, "alice"));
        Trainer upd = t.toBuilder().lastName("Johnson").build();

        Trainer res = dao.update(upd);

        assertThat(storage.get(1L).getLastName()).isEqualTo("Johnson");
        assertThat(users.get("alice")).isSameAs(res);
    }
}
