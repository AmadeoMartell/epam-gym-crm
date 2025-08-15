package com.epam.crm.model.rowmapper;

import com.epam.crm.model.TrainingType;

public class TrainingTypeRowMapper implements RowMapper<TrainingType> {
    @Override
    public TrainingType mapRow(String... row) throws RuntimeException {
        return new TrainingType(Long.parseLong(row[0]), row[1]);
    }

    @Override
    public Long getKey(TrainingType model) {
        return model.getId();
    }
}
