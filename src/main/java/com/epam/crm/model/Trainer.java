package com.epam.crm.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
public class Trainer extends User {
    private Long specializationId;
}
