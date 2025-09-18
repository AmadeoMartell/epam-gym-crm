package com.epam.crm.service;

import com.epam.crm.model.*;
import com.epam.crm.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock TrainingRepository trainingRepository;
    @Mock TraineeRepository traineeRepository;
    @Mock TrainerRepository trainerRepository;
    @Mock TrainingTypeRepository trainingTypeRepository;

    @InjectMocks TrainingService trainingService;

    @Test
    void addTraining_ok() {
        Trainee trainee = new Trainee(); trainee.setUsername("trainee1");
        Trainer trainer = new Trainer(); trainer.setUsername("trainer1");
        TrainingType type = new TrainingType(); type.setName("Cardio");

        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByName("Cardio")).thenReturn(Optional.of(type));
        when(trainingRepository.save(any(Training.class))).thenAnswer(inv -> inv.getArgument(0));

        Training saved = trainingService.addTraining(
                "trainee1", "trainer1", "Cardio", "Morning Run",
                LocalDate.of(2024,1,1), 60
        );

        assertEquals("Morning Run", saved.getName());
        assertEquals(60, saved.getDuration());
        assertEquals(LocalDate.of(2024,1,1), saved.getDate());
        assertSame(trainee, saved.getTrainee());
        assertSame(trainer, saved.getTrainer());
        assertSame(type, saved.getTrainingType());
    }

    @Test
    void addTraining_zeroDuration_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining("t","r","Type","n", LocalDate.now(), 0));
    }

    @Test
    void addTraining_nullDate_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                trainingService.addTraining("t","r","Type","n", null, 10));
    }

    @Test
    void addTraining_missingEntities_throwNotFound() {
        when(traineeRepository.findByUsername("trainee")).thenReturn(Optional.empty());
        assertThrows(java.util.NoSuchElementException.class, () ->
                trainingService.addTraining("trainee","trainer","Type","n", LocalDate.now(), 10));
    }
}
