package com.epam.crm.api.dto.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDate;


@Data
public class AddTrainingRequest {
    @NotBlank private String traineeUsername;
    @NotBlank private String trainerUsername;
    @NotBlank private String trainingTypeName;
    @NotBlank private String trainingName;

    @NotNull @Positive(message = "trainingDuration must be positive")
    private Integer trainingDuration;

    @NotNull
    private LocalDate trainingDate;
}

