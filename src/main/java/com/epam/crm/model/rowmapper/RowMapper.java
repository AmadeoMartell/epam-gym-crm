package com.epam.crm.model.rowmapper;

import java.util.List;

public interface RowMapper <T>{
    T mapRow(String... row) throws RuntimeException;

    Object getKey(T model);
}
