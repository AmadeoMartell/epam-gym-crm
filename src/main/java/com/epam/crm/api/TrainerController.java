package com.epam.crm.api;

import com.epam.crm.api.dto.trainer.TraineeShortDto;
import com.epam.crm.api.dto.trainer.TrainerProfileResponse;
import com.epam.crm.api.dto.trainer.UpdateTrainerRequest;
import com.epam.crm.model.Trainee;
import com.epam.crm.model.Trainer;
import com.epam.crm.service.AuthService;
import com.epam.crm.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private final AuthService authService;

    @GetMapping("/profile")
    public ResponseEntity<TrainerProfileResponse> getProfile(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @RequestParam String username
    ) {
        authService.authenticate(authUsername, authPassword);
        Trainer trainer = trainerService.findByUsernameOrThrow(username);
        return ResponseEntity.ok(toResponse(trainer));
    }

    @PutMapping("/profile")
    public ResponseEntity<TrainerProfileResponse> updateProfile(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @Valid @RequestBody UpdateTrainerRequest req
    ) {
        authService.authenticate(authUsername, authPassword);

        Trainer current = trainerService.findByUsernameOrThrow(req.getUsername());
        String specialization = current.getSpecialization();

        Trainer updated = trainerService.updateProfile(
                req.getUsername(),
                req.getFirstName(),
                req.getLastName(),
                specialization
        );

        Boolean cur = updated.getIsActive();
        if (cur == null || !cur.equals(req.getIsActive())) {
            if (Boolean.TRUE.equals(req.getIsActive())) {
                trainerService.activate(req.getUsername());
            } else {
                trainerService.deactivate(req.getUsername());
            }
            updated = trainerService.findByUsernameOrThrow(req.getUsername());
        }

        return ResponseEntity.ok(toResponse(updated));
    }

    private TrainerProfileResponse toResponse(Trainer trainer) {
        TrainerProfileResponse trainerProfileResponse = new TrainerProfileResponse();
        trainerProfileResponse.setFirstName(trainer.getFirstName());
        trainerProfileResponse.setLastName(trainer.getLastName());
        trainerProfileResponse.setSpecialization(trainer.getSpecialization());
        trainerProfileResponse.setIsActive(trainer.getIsActive());
        trainerProfileResponse.setTrainees(
                trainer.getTrainees() == null ? java.util.List.of() :
                        trainer.getTrainees().stream()
                                .map(this::toTraineeShort)
                                .collect(Collectors.toList())
        );
        return trainerProfileResponse;
    }

    private TraineeShortDto toTraineeShort(Trainee trainee) {
        return new TraineeShortDto(
                trainee.getUsername(),
                trainee.getFirstName(),
                trainee.getLastName()
        );
    }
}
