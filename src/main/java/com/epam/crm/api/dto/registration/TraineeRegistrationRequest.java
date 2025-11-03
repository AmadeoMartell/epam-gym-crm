package com.epam.crm.api.dto.registration;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


import java.time.LocalDate;


@Data
public class TraineeRegistrationRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private LocalDate dateOfBirth;

    private String address;
}