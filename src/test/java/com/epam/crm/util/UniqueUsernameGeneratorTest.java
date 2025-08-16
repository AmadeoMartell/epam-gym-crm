package com.epam.crm.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UniqueUsernameGeneratorTest {

    @Test
    void returnsBaseWhenNotTaken() {
        Set<String> existing = new HashSet<>();
        String result = UniqueUsernameGenerator.generate(existing, "john", "doe");
        assertThat(result).isEqualTo("john.doe");
    }

    @Test
    void appendsCounterWhenTaken() {
        Set<String> existing = new HashSet<>();
        existing.add("john.doe");

        String result = UniqueUsernameGenerator.generate(existing, "john", "doe");
        assertThat(result).isEqualTo("john.doe1");
    }

    @Test
    void incrementsUntilFree() {
        Set<String> existing = new HashSet<>();
        existing.add("john.doe");
        existing.add("john.doe1");
        existing.add("john.doe2");

        String result = UniqueUsernameGenerator.generate(existing, "john", "doe");
        assertThat(result).isEqualTo("john.doe3");
    }
}
