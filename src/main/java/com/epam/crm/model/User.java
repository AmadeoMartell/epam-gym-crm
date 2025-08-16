package com.epam.crm.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private Boolean active;
}
