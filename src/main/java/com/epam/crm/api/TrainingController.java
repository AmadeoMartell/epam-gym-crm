package com.epam.crm.api;

import com.epam.crm.api.dto.training.TraineeTrainingItemDto;
import com.epam.crm.api.dto.training.TrainerTrainingItemDto;
import com.epam.crm.model.Training;
import com.epam.crm.model.TrainingType;
import com.epam.crm.model.Trainer;
import com.epam.crm.service.AuthService;
import com.epam.crm.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TrainingController {


    private final TrainingService trainingService;
    private final AuthService authService;


    @GetMapping("/trainees/trainings")
    public ResponseEntity<List<TraineeTrainingItemDto>> getTraineeTrainings(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @RequestParam String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingType
    ) {
        authService.authenticate(authUsername, authPassword);


        List<Training> items = trainingService.findForTraineeWithCriteria(
                username, periodFrom, periodTo, trainerName, trainingType
        );

        return ResponseEntity.ok(items.stream().map(this::toDto).toList());
    }

    @GetMapping("/trainers/trainings")
    public ResponseEntity<java.util.List<TrainerTrainingItemDto>> getTrainerTrainings(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @RequestParam String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate periodTo,
            @RequestParam(required = false) String traineeName
    ) {
        authService.authenticate(authUsername, authPassword);


        java.util.List<com.epam.crm.model.Training> items = trainingService.findForTrainerWithCriteria(
                username, periodFrom, periodTo, traineeName
        );


        return ResponseEntity.ok(items.stream().map(this::toTrainerDto).toList());
    }


    private TrainerTrainingItemDto toTrainerDto(com.epam.crm.model.Training t) {
        String typeName = t.getTrainingType() == null ? null : t.getTrainingType().getName();
        String traineeFullName = (t.getTrainee() == null) ? null :
                ((t.getTrainee().getFirstName() == null ? "" : t.getTrainee().getFirstName()) + " " +
                        (t.getTrainee().getLastName() == null ? "" : t.getTrainee().getLastName())).trim();
        return new TrainerTrainingItemDto(
                t.getName(),
                t.getDate(),
                typeName,
                t.getDuration(),
                traineeFullName
        );
    }

    private TraineeTrainingItemDto toDto(Training t) {
        String typeName = Optional.ofNullable(t.getTrainingType())
                .map(TrainingType::getName)
                .orElse(null);


        String trainerFullName = Optional.ofNullable(t.getTrainer())
                .map(tr -> (nullToEmpty(tr.getFirstName()) + " " + nullToEmpty(tr.getLastName())).trim())
                .orElse(null);


        return new TraineeTrainingItemDto(
                t.getName(),
                t.getDate(),
                typeName,
                t.getDuration(),
                trainerFullName
        );
    }


    private static String nullToEmpty(String s) { return s == null ? "" : s; }
}
