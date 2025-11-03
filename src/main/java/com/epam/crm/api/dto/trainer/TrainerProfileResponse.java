package com.epam.crm.api.dto.trainer;

import lombok.Data;

import java.util.List;

@Data
public class TrainerProfileResponse {
    private String firstName;
    private String lastName;
    private String specialization;
    private Boolean isActive;
    private List<TraineeShortDto> trainees;
}
