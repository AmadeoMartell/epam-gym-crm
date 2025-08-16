package com.epam.crm.util;

import com.epam.crm.model.rowmapper.RowMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class FileStorageParserTest {

    private static final class TestModel {
        final String id;
        final String name;

        TestModel(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private static final class TestModelRowMapper implements RowMapper<TestModel> {
        @Override
        public TestModel mapRow(String... row) {
            String[] parts = (row.length == 1) ? row[0].split("\\s*,\\s*") : row;
            if (parts.length < 2) throw new IllegalArgumentException("Need id,name");
            return new TestModel(parts[0], parts[1]);
        }

        @Override
        public String getKey(TestModel model) {
            return model.id;
        }
    }

    @TempDir
    Path tempDir;

    @Test
    void parsesFileAndPopulatesMapSkippingBadLines() throws IOException {
        Path file = tempDir.resolve("sample.csv");
        List<String> lines = List.of(
                "u1,John",
                "u2,Jane",
                "bad_line_without_comma",
                "u3,Bob"
        );
        Files.write(file, lines);

        Map<String, TestModel> target = new HashMap<>();
        FileStorageParser.parseFile(file, target, new TestModelRowMapper());

        assertThat(target).hasSize(3);
        assertThat(target.keySet()).containsExactlyInAnyOrder("u1", "u2", "u3");
        assertThat(target.get("u2").name).isEqualTo("Jane");
    }
}
