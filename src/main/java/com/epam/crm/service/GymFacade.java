package com.epam.crm.service;

import com.epam.crm.model.Trainee;
import com.epam.crm.model.Trainer;
import com.epam.crm.model.Training;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public Optional<Trainee> getTrainee(Long id) {
        return traineeService.get(id);
    }

    public List<Trainee> listTrainees() {
        return traineeService.list();
    }

    public Optional<Trainer> getTrainer(Long id) {
        return trainerService.get(id);
    }

    public List<Trainer> listTrainers() {
        return trainerService.list();
    }

    public Optional<Training> getTraining(Long id) {
        return trainingService.get(id);
    }

    public List<Training> listTrainings() {
        return trainingService.list();
    }
}