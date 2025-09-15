package com.epam.crm.service;

import com.epam.crm.config.ApplicationConfig;
import com.epam.crm.model.Trainee;
import com.epam.crm.model.Trainer;
import com.epam.crm.model.Training;
import com.epam.crm.model.TrainingType;
import com.epam.crm.repository.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApplicationConfig.class})
@TestPropertySource("classpath:application.properties")
@Transactional
class GymFacadeIT {

    @Autowired GymFacade facade;
    @Autowired TrainingTypeRepository trainingTypeRepository;

    private TrainerService.CreatedAccount trainerAcc;
    private TraineeService.CreatedAccount traineeAcc;

    @BeforeEach
    void setup() {
        TrainingType type = new TrainingType();
        type.setName("Cardio");
        trainingTypeRepository.save(type);

        // create trainer & trainee
        trainerAcc = facade.createTrainerProfile("John","Doe","Fitness");
        traineeAcc = facade.createTraineeProfile("Ann","Lee", LocalDate.of(2000,1,1),"KZ");
    }

    @Test
    void fullScenario_trainerTraineeTrainingLifecycle() {
        // === 3–4 match credentials
        assertThat(facade.matchCredentials(trainerAcc.getUsername(), trainerAcc.getPassword())).isTrue();
        assertThat(facade.matchCredentials(traineeAcc.getUsername(), traineeAcc.getPassword())).isTrue();

        // === 5–6 get profiles
        Trainer trainer = facade.getTrainerByUsername(trainerAcc.getUsername(), trainerAcc.getPassword(), trainerAcc.getUsername());
        Trainee trainee = facade.getTraineeByUsername(traineeAcc.getUsername(), traineeAcc.getPassword(), traineeAcc.getUsername());
        assertThat(trainer.getUsername()).isEqualTo(trainerAcc.getUsername());
        assertThat(trainee.getUsername()).isEqualTo(traineeAcc.getUsername());

        // === 7–8 change password (trainee)
        facade.changePassword(traineeAcc.getUsername(), traineeAcc.getPassword(), "newPass123");
        assertThat(facade.matchCredentials(traineeAcc.getUsername(), "newPass123")).isTrue();

        // === 9–10 update profiles
        Trainee updatedTrainee = facade.updateTraineeProfile(trainerAcc.getUsername(), trainerAcc.getPassword(),
                traineeAcc.getUsername(),"Ann","Smith", LocalDate.of(2000,1,1),"NewAddr");
        assertThat(updatedTrainee.getLastName()).isEqualTo("Smith");

        Trainer updatedTrainer = facade.updateTrainerProfile(traineeAcc.getUsername(),"newPass123",
                trainerAcc.getUsername(),"John","Doe","Yoga");
        assertThat(updatedTrainer.getSpecialization()).isEqualTo("Yoga");

        // === 11–12 activate/deactivate
        facade.deactivate(trainerAcc.getUsername(), trainerAcc.getPassword(), traineeAcc.getUsername());
        assertThatThrownBy(() -> facade.deactivate(trainerAcc.getUsername(), trainerAcc.getPassword(), traineeAcc.getUsername()))
                .isInstanceOf(IllegalStateException.class);
        facade.activate(trainerAcc.getUsername(), trainerAcc.getPassword(), traineeAcc.getUsername());

        // === 16 add training
        Training training = facade.addTraining(trainerAcc.getUsername(), trainerAcc.getPassword(),
                traineeAcc.getUsername(), trainerAcc.getUsername(),
                "Cardio","Morning Run", LocalDate.now(), 45);
        assertThat(training.getId()).isNotNull();

        // === 14 trainee training list
        List<Training> traineeList = facade.listTrainingsForTrainee(trainerAcc.getUsername(), trainerAcc.getPassword(),
                traineeAcc.getUsername(), LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), "John", "Cardio");
        assertThat(traineeList).hasSize(1);

        // === 15 trainer training list
        List<Training> trainerList = facade.listTrainingsForTrainer(traineeAcc.getUsername(),"newPass123",
                trainerAcc.getUsername(), LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), "Ann");
        assertThat(trainerList).hasSize(1);

        // === 17 get unassigned trainers
        List<Trainer> unassigned = facade.getUnassignedTrainers(
                traineeAcc.getUsername(), "newPass123", traineeAcc.getUsername());

        assertThat(unassigned)
                .extracting(Trainer::getUsername)
                .contains(trainerAcc.getUsername());


        // === 18 update trainee’s trainers list
        facade.updateTraineeTrainers(trainerAcc.getUsername(), trainerAcc.getPassword(),
                traineeAcc.getUsername(), List.of(trainerAcc.getUsername()));

        Trainee withTrainers = facade.getTraineeByUsername(
                trainerAcc.getUsername(), trainerAcc.getPassword(), traineeAcc.getUsername());

        assertThat(withTrainers.getTrainers())
                .extracting(Trainer::getUsername)
                .contains(trainerAcc.getUsername());


        // === 13 delete trainee
        facade.deleteTrainee(trainerAcc.getUsername(), trainerAcc.getPassword(), traineeAcc.getUsername());
        assertThatThrownBy(() -> facade.getTraineeByUsername(trainerAcc.getUsername(), trainerAcc.getPassword(), traineeAcc.getUsername()))
                .isInstanceOf(RuntimeException.class);
    }
}
