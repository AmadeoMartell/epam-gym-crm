package com.epam.crm.model.rowmapper;

import com.epam.crm.model.Training;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;

@Component
public class TrainingRowMapper implements RowMapper<Training> {
    @Override
    public Training mapRow(String... row) throws RuntimeException {
        return Training.builder()
                .id(Long.parseLong(row[0]))
                .traineeId(Long.parseLong(row[1]))
                .trainerId(Long.parseLong(row[2]))
                .name(row[3])
                .trainingTypeId(Long.parseLong(row[4]))
                .date(LocalDate.parse(row[5]))
                .duration(Duration.parse(row[6]))
                .build();
    }

    @Override
    public Long getKey(Training model) {
        return model.getId();
    }
}
