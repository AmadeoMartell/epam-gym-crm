package com.epam.crm.model.rowmapper;

import com.epam.crm.model.Trainee;

import java.time.LocalDate;

public class TraineeRowMapper implements RowMapper<Trainee> {
    @Override
    public Trainee mapRow(String... row) throws RuntimeException {
        return Trainee.builder()
                .id(Long.parseLong(row[0]))
                .firstName(row[1])
                .lastName(row[2])
                .username(row[3])
                .password(row[4])
                .active(Boolean.parseBoolean(row[5]))
                .dateOfBirth(LocalDate.parse(row[6]))
                .address(row[7])
                .build();
    }

    @Override
    public Long getKey(Trainee model) {
        return model.getId();
    }
}
