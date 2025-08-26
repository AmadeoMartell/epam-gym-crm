package com.epam.crm.service;

import com.epam.crm.model.Trainee;
import com.epam.crm.model.Trainer;
import com.epam.crm.model.User;
import com.epam.crm.repository.TraineeRepository;
import com.epam.crm.repository.TrainerRepository;
import com.epam.crm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceTest {

    @Mock
    TraineeRepository traineeRepository;
    @Mock
    TrainerRepository trainerRepository;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    TraineeService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainee_generatesUsernameAndPassword_andPersists() {
        when(userRepository.findAll()).thenReturn(List.of());
        Trainee saved = new Trainee();
        saved.setId(1L);
        saved.setFirstName("Ann");
        saved.setLastName("Lee");
        saved.setUsername("ann.lee");
        saved.setPassword("pass");
        saved.setIsActive(true);
        when(traineeRepository.save(any(Trainee.class))).thenReturn(saved);

        var result = service.createTrainee("Ann", "Lee", LocalDate.of(2000, 1, 1), "KZ");

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUsername()).isNotBlank();
        assertThat(result.getPassword()).isNotBlank();
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void changePassword_ok_whenOldMatches() {
        User u = new User(1L, "A", "B", "ab", "old", true);
        when(userRepository.findByUsername("ab")).thenReturn(Optional.of(u));

        service.changePassword("ab", "old", "new123");

        assertThat(u.getPassword()).isEqualTo("new123");
        verify(userRepository).save(u);
    }

    @Test
    void changePassword_throws_whenOldWrong() {
        User u = new User(1L, "A", "B", "ab", "old", true);
        when(userRepository.findByUsername("ab")).thenReturn(Optional.of(u));

        assertThatThrownBy(() -> service.changePassword("ab", "WRONG", "new"))
                .isInstanceOf(SecurityException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateTraineeTrainers_replacesSet() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");
        Trainer t1 = new Trainer();
        t1.setId(10L);
        t1.setUsername("t1");
        Trainer t2 = new Trainer();
        t2.setId(20L);
        t2.setUsername("t2");
        trainee.addTrainer(t1);

        when(traineeRepository.findWithTrainersByUsername("trainee1"))
                .thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("t2"))
                .thenReturn(Optional.of(t2));

        service.updateTraineeTrainers("trainee1", List.of("t2"));

        assertThat(trainee.getTrainers()).containsExactly(t2);
        verify(traineeRepository).save(trainee);
    }

    @Test
    void getUnassignedTrainers_returnsComplement() {
        Trainee trainee = new Trainee();
        Trainer t1 = new Trainer();
        t1.setId(1L);
        Trainer t2 = new Trainer();
        t2.setId(2L);
        trainee.addTrainer(t1);

        when(traineeRepository.findWithTrainersByUsername("u")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAll()).thenReturn(List.of(t1, t2));

        var res = service.getUnassignedTrainersForTrainee("u");
        assertThat(res).containsExactly(t2);
    }
}
