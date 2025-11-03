package com.epam.crm.api.dto.trainee;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TraineeProfileResponse {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    private Boolean isActive;
    private List<TrainerShortDto> trainers;
}
