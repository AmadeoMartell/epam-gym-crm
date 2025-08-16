package com.epam.crm.service;

import com.epam.crm.dao.TrainingDao;
import com.epam.crm.model.Training;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    TrainingDao trainingDao;

    @InjectMocks
    TrainingService trainingService;

    private Training training(long id, long traineeId, long trainerId, long typeId, String name) {
        return Training.builder()
                .id(id)
                .traineeId(traineeId)
                .trainerId(trainerId)
                .trainingTypeId(typeId)
                .name(name)
                .date(LocalDate.of(2025, 1, 1))
                .duration(Duration.ofMinutes(60))
                .build();
    }

    @Test
    void create_delegatesToDao() {
        Training t = training(1L, 10L, 100L, 7L, "Cardio");
        when(trainingDao.create(t)).thenReturn(t);

        Training res = trainingService.create(t);

        assertThat(res).isSameAs(t);
        verify(trainingDao).create(t);
    }

    @Test
    void get_delegatesToDao() {
        Training t = training(2L, 11L, 101L, 8L, "Strength");
        when(trainingDao.findById(2L)).thenReturn(Optional.of(t));

        Optional<Training> res = trainingService.get(2L);

        assertThat(res).contains(t);
        verify(trainingDao).findById(2L);
    }

    @Test
    void list_delegatesToDao() {
        List<Training> list = List.of(
                training(1L, 10L, 100L, 7L, "Cardio"),
                training(2L, 11L, 101L, 8L, "Strength")
        );
        when(trainingDao.findAll()).thenReturn(list);

        List<Training> res = trainingService.list();

        assertThat(res).isEqualTo(list);
        verify(trainingDao).findAll();
    }
}
