package com.epam.crm.config;


import com.epam.crm.repository.TraineeRepository;
import com.epam.crm.repository.TrainerRepository;
import com.epam.crm.repository.TrainingRepository;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter traineeCreatedCounter(MeterRegistry registry) {
        return Counter.builder("app_trainee_created_total")
                .description("Total number of created trainee profiles")
                .register(registry);
    }

    @Bean
    public Counter trainerCreatedCounter(MeterRegistry registry) {
        return Counter.builder("app_trainer_created_total")
                .description("Total number of created trainer profiles")
                .register(registry);
    }

    @Bean
    public MeterBinder entityCountGauges(TraineeRepository traineeRepo,
                                         TrainerRepository trainerRepo,
                                         TrainingRepository trainingRepo) {
        return registry -> {
            Gauge.builder("app_trainee_count", traineeRepo, CrudRepository::count)
                    .description("Current number of trainees")
                    .register(registry);

            Gauge.builder("app_trainer_count", trainerRepo, CrudRepository::count)
                    .description("Current number of trainers")
                    .register(registry);

            Gauge.builder("app_training_count", trainingRepo, CrudRepository::count)
                    .description("Current number of trainings")
                    .register(registry);
        };
    }
}
