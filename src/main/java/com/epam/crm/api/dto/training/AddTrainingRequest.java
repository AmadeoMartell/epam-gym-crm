package com.epam.crm.api.dto.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDate;


@Data
public class AddTrainingRequest {
    @NotBlank private String traineeUsername;
    @NotBlank private String trainerUsername;
    @NotBlank private String trainingTypeName;
    @NotBlank private String trainingName;
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate trainingDate;
    @NotNull private Integer trainingDuration;
}
