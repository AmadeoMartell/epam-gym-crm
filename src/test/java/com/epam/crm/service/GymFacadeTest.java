package com.epam.crm.service;

import com.epam.crm.model.Trainer;
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
class GymFacadeTest {

    @Mock TraineeService traineeService;
    @Mock TrainerService trainerService;
    @Mock TrainingService trainingService;

    @InjectMocks GymFacade facade;

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
    void getTrainer_delegatesToTrainerService() {
        Trainer tr = trainer(10L, "alice");
        when(trainerService.get(10L)).thenReturn(Optional.of(tr));

        Optional<Trainer> res = facade.getTrainer(10L);

        assertThat(res).contains(tr);
        verify(trainerService).get(10L);
    }

    @Test
    void listTrainers_delegatesToTrainerService() {
        List<Trainer> list = List.of(trainer(1L, "a"), trainer(2L, "b"));
        when(trainerService.list()).thenReturn(list);

        List<Trainer> res = facade.listTrainers();

        assertThat(res).isEqualTo(list);
        verify(trainerService).list();
    }

    @Test
    void getTraining_delegatesToTrainingService() {
        Training t = training(5L, 10L, 100L, 7L, "Cardio");
        when(trainingService.get(5L)).thenReturn(Optional.of(t));

        Optional<Training> res = facade.getTraining(5L);

        assertThat(res).contains(t);
        verify(trainingService).get(5L);
    }

    @Test
    void listTrainings_delegatesToTrainingService() {
        List<Training> list = List.of(
                training(1L, 10L, 100L, 7L, "Cardio"),
                training(2L, 11L, 101L, 8L, "Strength")
        );
        when(trainingService.list()).thenReturn(list);

        List<Training> res = facade.listTrainings();

        assertThat(res).isEqualTo(list);
        verify(trainingService).list();
    }
}
