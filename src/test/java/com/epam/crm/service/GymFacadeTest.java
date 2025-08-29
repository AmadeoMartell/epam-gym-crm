package com.epam.crm.service;

import com.epam.crm.model.Trainee;
import com.epam.crm.model.Trainer;
import com.epam.crm.model.Training;
import com.epam.crm.model.User;
import com.epam.crm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class GymFacadeTest {

    @Mock UserRepository userRepository;
    @Mock TraineeService traineeService;
    @Mock TrainerService trainerService;
    @Mock TrainingService trainingService;

    @InjectMocks GymFacade facade;

    private User authUser;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        authUser = new User(1L,"Auth","User","auth","pw",true);
        when(userRepository.findByUsername("auth")).thenReturn(Optional.of(authUser));
    }


    @Test
    void createTraineeProfile_delegates() {
        var acc = new TraineeService.CreatedAccount(2L,"u","p");
        when(traineeService.createTrainee("Ann","Lee", LocalDate.of(2000,1,1),"KZ")).thenReturn(acc);

        var result = facade.createTraineeProfile("Ann","Lee", LocalDate.of(2000,1,1),"KZ");

        assertThat(result).isEqualTo(acc);
    }

    @Test
    void createTrainerProfile_delegates() {
        var acc = new TrainerService.CreatedAccount(1L,"u","p");
        when(trainerService.createTrainer("J","D","Spec")).thenReturn(acc);

        var result = facade.createTrainerProfile("J","D","Spec");

        assertThat(result).isEqualTo(acc);
    }


    @Test
    void matchCredentials_ok() {
        assertThat(facade.matchCredentials("auth","pw")).isTrue();
    }

    @Test
    void matchCredentials_false_whenWrong() {
        assertThat(facade.matchCredentials("auth","wrong")).isFalse();
    }


    @Test
    void getTraineeByUsername_checksAuthAndDelegates() {
        Trainee tr = new Trainee(); tr.setUsername("trainee1");
        when(traineeService.findByUsername("trainee1")).thenReturn(Optional.of(tr));

        var result = facade.getTraineeByUsername("auth","pw","trainee1");
        assertThat(result.getUsername()).isEqualTo("trainee1");
    }

    @Test
    void getTrainerByUsername_checksAuthAndDelegates() {
        Trainer tr = new Trainer(); tr.setUsername("trainer1");
        when(trainerService.findByUsernameOrThrow("trainer1")).thenReturn(tr);

        var result = facade.getTrainerByUsername("auth","pw","trainer1");
        assertThat(result.getUsername()).isEqualTo("trainer1");
    }


    @Test
    void changePassword_delegatesToTraineeService() {
        facade.changePassword("u","old","new");
        verify(traineeService).changePassword("u","old","new");
    }


    @Test
    void updateTraineeProfile_checksAuthAndDelegates() {
        Trainee tr = new Trainee(); tr.setUsername("t");
        when(traineeService.updateProfile("t","F","L",null,"Addr")).thenReturn(tr);

        var res = facade.updateTraineeProfile("auth","pw","t","F","L",null,"Addr");
        assertThat(res).isEqualTo(tr);
    }

    @Test
    void updateTrainerProfile_checksAuthAndDelegates() {
        Trainer tr = new Trainer(); tr.setUsername("x");
        when(trainerService.updateProfile("x","F","L","Spec")).thenReturn(tr);

        var res = facade.updateTrainerProfile("auth","pw","x","F","L","Spec");
        assertThat(res).isEqualTo(tr);
    }


    @Test
    void activate_delegates() {
        facade.activate("auth","pw","target");
        verify(traineeService).activate("target");
    }

    @Test
    void deactivate_delegates() {
        facade.deactivate("auth","pw","target");
        verify(traineeService).deactivate("target");
    }


    @Test
    void deleteTrainee_delegates() {
        facade.deleteTrainee("auth","pw","traineeX");
        verify(traineeService).deleteByUsername("traineeX");
    }


    @Test
    void addTraining_checksAuthAndDelegates() {
        Training t = new Training(); t.setId(10L);
        when(trainingService.addTraining("trainee","trainer","Type","Name", LocalDate.now(), 30)).thenReturn(t);

        var res = facade.addTraining("auth","pw","trainee","trainer","Type","Name", LocalDate.now(),30);
        assertThat(res.getId()).isEqualTo(10L);
    }

    @Test
    void listTrainingsForTrainee_delegates() {
        Training t = new Training();
        when(trainingService.findForTraineeWithCriteria("trainee",null,null,null,null)).thenReturn(List.of(t));

        var res = facade.listTrainingsForTrainee("auth","pw","trainee",null,null,null,null);
        assertThat(res).hasSize(1);
    }

    @Test
    void listTrainingsForTrainer_delegates() {
        Training t = new Training();
        when(trainingService.findForTrainerWithCriteria("trainer",null,null,null)).thenReturn(List.of(t));

        var res = facade.listTrainingsForTrainer("auth","pw","trainer",null,null,null);
        assertThat(res).hasSize(1);
    }

    @Test
    void getUnassignedTrainers_delegates() {
        Trainer tr = new Trainer(); tr.setUsername("free");
        when(traineeService.getUnassignedTrainersForTrainee("trainee1")).thenReturn(List.of(tr));

        var res = facade.getUnassignedTrainers("auth","pw","trainee1");
        assertThat(res).containsExactly(tr);
    }

    @Test
    void updateTraineeTrainers_delegates() {
        facade.updateTraineeTrainers("auth","pw","trainee1", List.of("t1","t2"));
        verify(traineeService).updateTraineeTrainers("trainee1", List.of("t1","t2"));
    }

    @Test
    void requireAuth_throwsWhenWrongPassword() {
        authUser.setPassword("correct");
        assertThatThrownBy(() -> facade.getTraineeByUsername("auth","wrong","x"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void requireAuth_throwsWhenUserNotFound() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> facade.getTrainerByUsername("missing","pw","x"))
                .isInstanceOf(NoSuchElementException.class);
    }
}
