package com.epam.crm.api.dto.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTraineeRequest {
    @NotBlank private String username;
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    @NotNull private Boolean isActive;
}
