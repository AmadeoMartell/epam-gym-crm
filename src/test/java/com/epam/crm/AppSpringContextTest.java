package com.epam.crm;

import com.epam.crm.config.ApplicationConfig;
import com.epam.crm.dao.TrainingDao;
import com.epam.crm.model.*;
import com.epam.crm.service.GymFacade;
import com.epam.crm.service.TraineeService;
import com.epam.crm.service.TrainerService;
import com.epam.crm.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApplicationConfig.class, AppSpringContextTest.TestSupportConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AppSpringContextTest {

    @Configuration
    static class TestSupportConfig {
        @Bean
        static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }

    @Autowired
    TraineeService traineeService;
    @Autowired
    TrainerService trainerService;
    @Autowired
    TrainingService trainingService;
    @Autowired
    GymFacade facade;

    @Autowired
    @Qualifier("trainees")
    Map<Long, Trainee> traineeStorage;
    @Autowired
    @Qualifier("trainers")
    Map<Long, Trainer> trainerStorage;
    @Autowired
    @Qualifier("trainings")
    Map<Long, Training> trainingStorage;
    @Autowired
    @Qualifier("trainingTypes")
    Map<Long, TrainingType> trainingTypeStorage;
    @Autowired
    @Qualifier("users")
    Map<String, User> usersStorage;

    @Autowired
    TrainingDao trainingDao;

    @Test
    void loadsRealDataFromFiles_viaApplicationProperties() {
        assertThat(traineeStorage).isNotEmpty();
        assertThat(trainerStorage).isNotEmpty();
        assertThat(trainingStorage).isNotEmpty();
        assertThat(trainingTypeStorage).isNotEmpty();

        assertThat(traineeStorage).containsKeys(1L, 2L);
        assertThat(trainerStorage).containsKeys(5L, 6L);
        assertThat(trainingStorage).containsKeys(1L, 2L);
        assertThat(trainingTypeStorage).containsKeys(1L, 2L);

        assertThat(usersStorage.keySet())
                .contains("Jhon.Doe", "Jane.Doe", "Jack.Smith", "Martha.Kent");
    }

    @Test
    void services_and_facade_canReadSeedData() {
        assertThat(traineeService.list()).extracting(Trainee::getId).contains(1L, 2L);
        assertThat(trainerService.list()).extracting(Trainer::getId).contains(5L, 6L);
        assertThat(trainingService.list()).extracting(Training::getId).contains(1L, 2L);

        assertThat(facade.getTrainer(5L)).isPresent();
        assertThat(facade.getTraining(2L)).isPresent();
        assertThat(facade.listTrainers()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(facade.listTrainings()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void dao_filtersByTrainee_andByTrainer() {
        List<Training> byTrainee1 = trainingDao.findByTrainee(1L);
        List<Training> byTrainer6 = trainingDao.findByTrainer(6L);

        assertThat(byTrainee1).allMatch(t -> t.getTraineeId().equals(1L));
        assertThat(byTrainer6).allMatch(t -> t.getTrainerId().equals(6L));
    }

    @Test
    void createTraining_viaService_isVisibleThroughFacadeAndFilters() {
        Training t = Training.builder()
                .id(3L)
                .traineeId(1L)
                .trainerId(6L)
                .name("Stretching")
                .trainingTypeId(1L)
                .date(LocalDate.now())
                .duration(Duration.ofMinutes(45))
                .build();

        trainingService.create(t);

        assertThat(facade.listTrainings())
                .extracting(Training::getId)
                .contains(3L);

        assertThat(trainingDao.findByTrainer(6L))
                .extracting(Training::getId)
                .contains(3L);
    }

    @Test
    void updateTrainer_persistsChanges() {
        Trainer original = trainerService.get(5L).orElseThrow();
        Trainer updated = original.toBuilder()
                .lastName("Sparrow")
                .specializationId(3L)
                .build();

        Trainer saved = trainerService.update(updated);

        assertThat(saved.getLastName()).isEqualTo("Sparrow");
        assertThat(trainerStorage.get(5L).getSpecializationId()).isEqualTo(3L);
    }

    @Test
    void deleteTrainee_removesFromTraineesAndUsersStorages() {
        assertThat(traineeStorage).containsKey(1L);
        assertThat(usersStorage).containsKey("Jhon.Doe");

        boolean removed = traineeService.delete(1L);
        assertThat(removed).isTrue();

        assertThat(traineeStorage).doesNotContainKey(1L);
        assertThat(usersStorage).doesNotContainKey("Jhon.Doe");
    }
}

