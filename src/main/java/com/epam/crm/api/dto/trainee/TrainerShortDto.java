package com.epam.crm.api.dto.trainee;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainerShortDto {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization;
}

