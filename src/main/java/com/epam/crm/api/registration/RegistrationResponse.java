package com.epam.crm.api.registration;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel("Registration response with generated credentials")
public class RegistrationResponse {
    @ApiModelProperty(value = "Generated username", example = "John.Smith")
    private String username;


    @ApiModelProperty(value = "Generated password", example = "aB9x3Qw7Zp")
    private String password;
}
