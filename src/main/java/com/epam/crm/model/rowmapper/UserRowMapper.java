package com.epam.crm.model.rowmapper;

import com.epam.crm.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(String... row) throws RuntimeException {
        return User.builder()
                .id(Long.parseLong(row[0]))
                .firstName(row[1])
                .lastName(row[2])
                .username(row[3])
                .password(row[4])
                .active(Boolean.parseBoolean(row[5]))
                .build();
    }

    @Override
    public String getKey(User model){
        return model.getUsername();
    }

}
