package com.epam.crm.dao;

import com.epam.crm.model.Trainee;
import com.epam.crm.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class TraineeDaoTest {

    private Map<Long, Trainee> storage;
    private Map<String, User> users;
    private TraineeDao dao;

    @BeforeEach
    void setUp() {
        storage = new HashMap<>();
        users = new HashMap<>();
        dao = new TraineeDao(storage, users);
    }

    private Trainee trainee(long id, String uname) {
        return Trainee.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .username(uname)
                .password("secret")
                .active(true)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Main St")
                .build();
    }

    @Test
    void create_putsIntoBothMaps_andReturnsEntity() {
        Trainee t = trainee(1L, "john.doe");

        Trainee created = dao.create(t);

        assertThat(created).isSameAs(t);
        assertThat(storage).containsEntry(1L, t);
        assertThat(users).containsEntry("john.doe", t);
    }

    @Test
    void findById_returnsPresentWhenExists_otherwiseEmpty() {
        Trainee t = trainee(1L, "john.doe");
        dao.create(t);

        assertThat(dao.findById(1L)).contains(t);
        assertThat(dao.findById(2L)).isEmpty();
    }

    @Test
    void findAll_returnsAllValues_snapshotCopy() {
        Trainee t1 = dao.create(trainee(1L, "john.doe"));
        Trainee t2 = dao.create(trainee(2L, "jane.doe"));

        List<Trainee> all = dao.findAll();

        assertThat(all).containsExactlyInAnyOrder(t1, t2);
        int sizeBefore = storage.size();
        all.clear();
        assertThat(storage).hasSize(sizeBefore);
    }

    @Test
    void update_overwritesStorage_andUsersMapping() {
        Trainee t = dao.create(trainee(1L, "john.doe"));
        Trainee updated = t.toBuilder().firstName("Johnny").build();

        Trainee res = dao.update(updated);

        assertThat(res.getFirstName()).isEqualTo("Johnny");
        assertThat(storage.get(1L).getFirstName()).isEqualTo("Johnny");
        assertThat(users.get("john.doe")).isSameAs(res);
    }

    @Test
    void delete_removesFromBothMaps_andReturnsTrueWhenRemoved() {
        dao.create(trainee(1L, "john.doe"));
        assertThat(dao.delete(1L)).isTrue();

        assertThat(storage).doesNotContainKey(1L);
        assertThat(users).doesNotContainKey("john.doe");
        assertThat(dao.delete(1L)).isFalse();
    }
}
