package com.epam.crm.api;

import com.epam.crm.api.dto.training.TrainingTypeDto;
import com.epam.crm.model.TrainingType;
import com.epam.crm.repository.TrainingTypeRepository;
import com.epam.crm.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/training-types")
public class TrainingTypeController {

    private final TrainingTypeRepository trainingTypeRepository;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<TrainingTypeDto>> getAll(
            @RequestHeader("X-Username") String authUsername,
            @RequestHeader("X-Password") String authPassword
    ) {
        authService.authenticate(authUsername, authPassword);

        List<TrainingTypeDto> resp = trainingTypeRepository.findAll()
                .stream()
                .map(tt -> new TrainingTypeDto(tt.getId(), tt.getName()))
                .toList();

        return ResponseEntity.ok(resp);
    }
}
