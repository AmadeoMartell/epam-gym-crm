package com.epam.crm.util;

import com.epam.crm.model.rowmapper.RowMapper;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Slf4j
public class FileStorageParser {
    @SuppressWarnings("unchecked")
    public static <K, V> void parseFile(Path path, Map<K, V> map, RowMapper<V> mapper) {
        try {
            for (String line : Files.readAllLines(path)) {
                var row = line.split(";");

                try {
                    V model = mapper.mapRow(row);
                    map.put((K) mapper.getKey(model), model);

                } catch (Exception e) {
                    log.error("Error while parsing file line: {}\nSkipping..", line, e);
                }

            }
        } catch (Exception e) {
            log.error("Error while parsing file {}", path.getFileName(), e);
        }
    }

}
