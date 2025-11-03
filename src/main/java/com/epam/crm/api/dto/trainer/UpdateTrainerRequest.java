package com.epam.crm.api.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTrainerRequest {
    @NotBlank private String username;
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotNull private Boolean isActive;
}
