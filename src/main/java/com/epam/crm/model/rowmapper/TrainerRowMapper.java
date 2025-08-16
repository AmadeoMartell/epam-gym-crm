package com.epam.crm.model.rowmapper;

import com.epam.crm.model.Trainer;
import org.springframework.stereotype.Component;

@Component
public class TrainerRowMapper implements RowMapper<Trainer> {
    @Override
    public Trainer mapRow(String... row) throws RuntimeException {
        return Trainer.builder()
                .id(Long.parseLong(row[0]))
                .firstName(row[1])
                .lastName(row[2])
                .username(row[3])
                .password(row[4])
                .active(Boolean.parseBoolean(row[5]))
                .specializationId(Long.parseLong(row[6]))
                .build();
    }

    @Override
    public Long getKey(Trainer model) {
        return model.getId();
    }
}
