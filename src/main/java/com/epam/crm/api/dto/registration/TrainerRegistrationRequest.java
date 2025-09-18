package com.epam.crm.api.dto.registration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TrainerRegistrationRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private String specialization;
}
