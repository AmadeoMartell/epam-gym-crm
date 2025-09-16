package com.epam.crm.api.dto.trainer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TraineeShortDto {
    private String username;
    private String firstName;
    private String lastName;
}
