package com.epam.crm.api.registration;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel("Trainer registration request")
public class TrainerRegistrationRequest {
    @NotBlank
    @ApiModelProperty(value = "First name", required = true, example = "Jane")
    private String firstName;


    @NotBlank
    @ApiModelProperty(value = "Last name", required = true, example = "Doe")
    private String lastName;


    @NotNull
    @ApiModelProperty(value = "Training type ID (specialization)", required = true, example = "1")
    private Long specializationId;
}
