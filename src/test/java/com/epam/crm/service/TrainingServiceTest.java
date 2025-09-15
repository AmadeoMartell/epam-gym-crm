package com.epam.crm.service;

import com.epam.crm.model.*;
import com.epam.crm.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    @Mock
    TrainingRepository trainingRepository;
    @Mock
    TraineeRepository traineeRepository;
    @Mock
    TrainerRepository trainerRepository;
    @Mock
    TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    TrainingService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTraining_validatesAndSaves() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");
        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setName("Cardio");

        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByName("Cardio")).thenReturn(Optional.of(type));
        when(trainingRepository.save(any(Training.class))).thenAnswer(inv -> {
            Training t = inv.getArgument(0);
            t.setId(777L);
            return t;
        });

        var saved = service.addTraining("trainee1", "trainer1", "Cardio",
                "Morning Run", LocalDate.now(), 45);

        assertThat(saved.getId()).isEqualTo(777L);
        assertThat(saved.getTrainingType().getName()).isEqualTo("Cardio");
    }

    @Test
    void addTraining_rejectsNonPositiveDuration() {
        assertThatThrownBy(() -> service.addTraining("t", "r", "x", "n", LocalDate.now(), 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

