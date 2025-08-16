package com.epam.crm.service;

import com.epam.crm.dao.TraineeDao;
import com.epam.crm.model.Trainee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    TraineeDao traineeDao;

    @InjectMocks
    TraineeService traineeService;

    private Trainee trainee(long id, String uname) {
        return Trainee.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .username(uname)
                .password("pwd")
                .active(true)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Main St")
                .build();
    }

    @Test
    void get_delegatesToDao() {
        Trainee t = trainee(1L, "john.doe");
        when(traineeDao.findById(1L)).thenReturn(Optional.of(t));

        Optional<Trainee> res = traineeService.get(1L);

        assertThat(res).contains(t);
        verify(traineeDao).findById(1L);
    }

    @Test
    void list_delegatesToDao() {
        List<Trainee> list = List.of(trainee(1L, "a"), trainee(2L, "b"));
        when(traineeDao.findAll()).thenReturn(list);

        List<Trainee> res = traineeService.list();

        assertThat(res).isEqualTo(list);
        verify(traineeDao).findAll();
    }

    @Test
    void delete_delegatesToDao() {
        when(traineeDao.delete(7L)).thenReturn(true);

        boolean ok = traineeService.delete(7L);

        assertThat(ok).isTrue();
        verify(traineeDao).delete(7L);
    }
}
