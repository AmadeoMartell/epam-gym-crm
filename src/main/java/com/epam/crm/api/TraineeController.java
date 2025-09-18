package com.epam.crm.api;

import com.epam.crm.api.dto.common.ActivationRequest;
import com.epam.crm.api.dto.trainee.TraineeProfileResponse;
import com.epam.crm.api.dto.trainee.TrainerShortDto;
import com.epam.crm.api.dto.trainee.UpdateTraineeRequest;
import com.epam.crm.api.dto.trainee.UpdateTraineeTrainersRequest;
import com.epam.crm.model.Trainee;
import com.epam.crm.model.Trainer;
import com.epam.crm.service.AuthService;
import com.epam.crm.service.TraineeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.util.stream.Collectors;

@Api(tags = "Trainee")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trainees")
public class TraineeController {

    private final TraineeService traineeService;
    private final AuthService authService;

    @ApiOperation("Get trainee profile")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    @GetMapping("/profile")
    public ResponseEntity<TraineeProfileResponse> getProfile(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @RequestParam String username
    ) {
        authService.authenticate(authUsername, authPassword);

        Trainee t = traineeService.findByUsername(username)
                .orElseThrow(() -> new java.util.NoSuchElementException("Trainee not found: " + username));

        return ResponseEntity.ok(toResponse(t));
    }

    @ApiOperation("Update trainee profile (isActive supported)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    @PutMapping("/profile")
    public ResponseEntity<TraineeProfileResponse> updateProfile(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @Valid @RequestBody UpdateTraineeRequest req
    ) {
        authService.authenticate(authUsername, authPassword);

        Trainee updated = traineeService.updateProfile(
                req.getUsername(),
                req.getFirstName(),
                req.getLastName(),
                req.getDateOfBirth(),
                req.getAddress()
        );

        Boolean current = updated.getIsActive();
        if (current == null || !current.equals(req.getIsActive())) {
            if (Boolean.TRUE.equals(req.getIsActive())) {
                traineeService.activate(req.getUsername());
            } else {
                traineeService.deactivate(req.getUsername());
            }

            updated = traineeService.findByUsername(req.getUsername())
                    .orElseThrow(() -> new java.util.NoSuchElementException("Trainee not found: " + req.getUsername()));
        }

        return ResponseEntity.ok(toResponse(updated));
    }

    @ApiOperation("Delete trainee profile (cascade delete trainings)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteProfile(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @RequestParam String username
    ) {
        authService.authenticate(authUsername, authPassword);
        traineeService.deleteByUsername(username);
        return ResponseEntity.ok().build();
    }

    private TraineeProfileResponse toResponse(Trainee t) {
        TraineeProfileResponse traineeProfileResponse = new TraineeProfileResponse();
        traineeProfileResponse.setFirstName(t.getFirstName());
        traineeProfileResponse.setLastName(t.getLastName());
        traineeProfileResponse.setDateOfBirth(t.getDateOfBirth());
        traineeProfileResponse.setAddress(t.getAddress());
        traineeProfileResponse.setIsActive(t.getIsActive());
        traineeProfileResponse.setTrainers(
                t.getTrainers() == null ? java.util.List.of() :
                        t.getTrainers().stream()
                                .map(this::toTrainerShort)
                                .collect(Collectors.toList())
        );
        return traineeProfileResponse;
    }

    private TrainerShortDto toTrainerShort(Trainer trainer) {
        return new TrainerShortDto(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getSpecialization()
        );
    }

    @ApiOperation("Get active trainers not assigned to the trainee")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    @GetMapping("/unassigned-trainers")
    public ResponseEntity<java.util.List<TrainerShortDto>> getUnassignedActiveTrainers(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @RequestParam String username
    ) {
        authService.authenticate(authUsername, authPassword);

        var trainers = traineeService.getUnassignedTrainersForTrainee(username)
                .stream()
                .filter(tr -> Boolean.TRUE.equals(tr.getIsActive()))
                .map(this::toTrainerShort)
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(trainers);
    }

    @ApiOperation("Update trainee's trainer list")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainee/Trainer not found")
    })
    @PutMapping("/trainers")
    public ResponseEntity<java.util.List<TrainerShortDto>> updateTraineeTrainers(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @Valid @RequestBody UpdateTraineeTrainersRequest req
    ) {
        authService.authenticate(authUsername, authPassword);

        traineeService.updateTraineeTrainers(req.getTraineeUsername(), req.getTrainers());

        var trainee = traineeService.findByUsername(req.getTraineeUsername())
                .orElseThrow(() -> new java.util.NoSuchElementException("Trainee not found: " + req.getTraineeUsername()));

        var resp = (trainee.getTrainers() == null ? java.util.List.<TrainerShortDto>of()
                : trainee.getTrainers().stream().map(this::toTrainerShort)
                .collect(java.util.stream.Collectors.toList()));

        return ResponseEntity.ok(resp);
    }

    @ApiOperation("Activate/Deactivate trainee (non-idempotent)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Trainee not found"),
            @ApiResponse(code = 409, message = "Already in requested state")
    })
    @PatchMapping("/activation")
    public ResponseEntity<Void> toggleTrainee(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword,
            @RequestBody @Valid ActivationRequest req
    ) {
        authService.authenticate(authUsername, authPassword);
        if (Boolean.TRUE.equals(req.getIsActive())) {
            traineeService.activate(req.getUsername());
        } else {
            traineeService.deactivate(req.getUsername());
        }
        return ResponseEntity.ok().build();
    }

}
