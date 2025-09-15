package com.epam.crm.api.dto.registration;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


import java.time.LocalDate;


@Data
@ApiModel("Trainee registration request")
public class TraineeRegistrationRequest {
    @NotBlank
    @ApiModelProperty(value = "First name", required = true, example = "John")
    private String firstName;

    @NotBlank
    @ApiModelProperty(value = "Last name", required = true, example = "Smith")
    private String lastName;


    @ApiModelProperty(value = "Date of birth (yyyy-MM-dd)", example = "2000-04-01")
    private LocalDate dateOfBirth;


    @ApiModelProperty(value = "Address", example = "221B Baker Street")
    private String address;
}