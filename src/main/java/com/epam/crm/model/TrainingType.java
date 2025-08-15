package com.epam.crm.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainingType {
    private Long id;
    private String trainingType;
}
