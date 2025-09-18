package com.epam.crm.api;

import com.epam.crm.api.dto.common.ActivationRequest;
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
import io.swagger.annotations.*;
import java.util.stream.Collectors;

@Api(tags = "Trainer")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private final AuthService authService;

    @ApiOperation("Get trainer profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    @GetMapping("/profile")
    public ResponseEntity<TrainerProfileResponse> getProfile(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @RequestParam("username") String username
    ) {
        authService.authenticate(authUsername, authPassword);
        Trainer trainer = trainerService.findByUsernameOrThrow(username);
        return ResponseEntity.ok(toResponse(trainer));
    }

    @ApiOperation("Update trainer profile (specialization is read-only)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
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

    @ApiOperation("Activate/Deactivate trainer (non-idempotent)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainer not found"),
            @ApiResponse(code = 409, message = "Already in requested state")
    })
    @PatchMapping("/activation")
    public ResponseEntity<Void> toggleTrainer(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @RequestBody @Valid ActivationRequest req
    ) {
        authService.authenticate(authUsername, authPassword);
        if (Boolean.TRUE.equals(req.getIsActive())) {
            trainerService.activate(req.getUsername());
        } else {
            trainerService.deactivate(req.getUsername());
        }
        return ResponseEntity.ok().build();
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
