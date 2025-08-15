package com.epam.crm.model;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
public class Training {
    private Long id;
    private Long traineeId;
    private Long trainerId;
    private Long trainingType;
    private LocalDate trainingDate;
    private Duration trainingDuration;
}
