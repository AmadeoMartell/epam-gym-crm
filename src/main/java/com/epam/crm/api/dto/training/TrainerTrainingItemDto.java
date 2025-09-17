package com.epam.crm.api.dto.training;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;


@Data
@AllArgsConstructor
public class TrainerTrainingItemDto {
    private String trainingName;
    private LocalDate trainingDate;
    private String trainingType;
    private Integer trainingDuration;
    private String traineeName;
}
