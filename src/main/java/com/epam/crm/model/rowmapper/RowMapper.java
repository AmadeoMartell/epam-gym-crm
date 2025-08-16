package com.epam.crm.model.rowmapper;

public interface RowMapper <T>{
    T mapRow(String... row) throws RuntimeException;

    Object getKey(T model);
}
