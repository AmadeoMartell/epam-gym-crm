package com.epam.crm.service;

import com.epam.crm.dao.TrainerDao;
import com.epam.crm.model.Trainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    TrainerDao trainerDao;

    @InjectMocks
    TrainerService trainerService;

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
    void get_delegatesToDao() {
        Trainer tr = trainer(10L, "alice");
        when(trainerDao.findById(10L)).thenReturn(Optional.of(tr));

        Optional<Trainer> res = trainerService.get(10L);

        assertThat(res).contains(tr);
        verify(trainerDao).findById(10L);
    }

    @Test
    void list_delegatesToDao() {
        List<Trainer> list = List.of(trainer(1L, "a"), trainer(2L, "b"));
        when(trainerDao.findAll()).thenReturn(list);

        List<Trainer> res = trainerService.list();

        assertThat(res).isEqualTo(list);
        verify(trainerDao).findAll();
    }

    @Test
    void update_delegatesToDao() {
        Trainer upd = trainer(3L, "charlie").toBuilder().lastName("Johnson").build();
        when(trainerDao.update(upd)).thenReturn(upd);

        Trainer res = trainerService.update(upd);

        assertThat(res.getLastName()).isEqualTo("Johnson");
        verify(trainerDao).update(upd);
    }
}
