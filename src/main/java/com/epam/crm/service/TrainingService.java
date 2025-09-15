package com.epam.crm.service;

import com.epam.crm.model.Trainee;
import com.epam.crm.model.Trainer;
import com.epam.crm.model.Training;
import com.epam.crm.model.TrainingType;
import com.epam.crm.repository.TraineeRepository;
import com.epam.crm.repository.TrainerRepository;
import com.epam.crm.repository.TrainingRepository;
import com.epam.crm.repository.TrainingTypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    @Transactional
    public Training addTraining(String traineeUsername, String trainerUsername, String trainingTypeName, String name, LocalDate date, int duration) {
        if (duration <= 0) throw new IllegalArgumentException("Duration must be positive");
        if (date == null) throw new IllegalArgumentException("Training date required");

        Trainee trainee = traineeRepository.findByUsername(traineeUsername).orElseThrow(() -> new NoSuchElementException("Trainee not found: " + traineeUsername));
        Trainer trainer = trainerRepository.findByUsername(trainerUsername).orElseThrow(() -> new NoSuchElementException("Trainer not found: " + trainerUsername));
        TrainingType type = trainingTypeRepository.findByName(trainingTypeName).orElseThrow(() -> new NoSuchElementException("TrainingType not found: " + trainingTypeName));

        Training tr = new Training();
        tr.setTrainee(trainee);
        tr.setTrainer(trainer);
        tr.setTrainingType(type);
        tr.setName(name);
        tr.setDate(date);
        tr.setDuration(duration);

        Training saved = trainingRepository.save(tr);
        log.info("Training added: id={}, trainee={}, trainer={}, type={}", saved.getId(), traineeUsername, trainerUsername, trainingTypeName);
        return saved;
    }

    public List<Training> findForTraineeWithCriteria(String traineeUsername, LocalDate from, LocalDate to, String trainerName, String typeName) {
        return trainingRepository.findForTraineeWithCriteria(traineeUsername, from, to, trainerName, typeName);
    }

    public List<Training> findForTrainerWithCriteria(String trainerUsername, LocalDate from, LocalDate to, String traineeName) {
        return trainingRepository.findForTrainerWithCriteria(trainerUsername, from, to, traineeName);
    }
}
