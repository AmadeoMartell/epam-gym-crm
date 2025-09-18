package com.epam.crm.api;

import com.epam.crm.api.dto.registration.RegistrationResponse;
import com.epam.crm.api.dto.registration.TraineeRegistrationRequest;
import com.epam.crm.api.dto.registration.TrainerRegistrationRequest;
import com.epam.crm.service.TraineeService;
import com.epam.crm.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RegistrationController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;

    @PostMapping("/trainees/register")
    public ResponseEntity<RegistrationResponse> registerTrainee(
            @Valid @RequestBody TraineeRegistrationRequest req) {

        var acc = traineeService.createTrainee(
                req.getFirstName().trim(),
                req.getLastName().trim(),
                req.getDateOfBirth(),
                req.getAddress()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegistrationResponse(acc.getUsername(), acc.getPassword()));
    }

    @PostMapping("/trainers/register")
    public ResponseEntity<RegistrationResponse> registerTrainer(
            @Valid @RequestBody TrainerRegistrationRequest req) {

        var acc = trainerService.createTrainer(
                req.getFirstName().trim(),
                req.getLastName().trim(),
                req.getSpecialization().trim()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegistrationResponse(acc.getUsername(), acc.getPassword()));
    }
}
