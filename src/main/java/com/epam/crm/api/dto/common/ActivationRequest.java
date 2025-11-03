package com.epam.crm.api.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActivationRequest {
    @NotBlank
    private String username;
    @NotNull
    private Boolean isActive;
}
