package com.epam.crm.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordGeneratorTest {

    @Test
    void generatesStringOfRequestedLengthAndAllowedChars() {
        int len = 24;
        String pwd = PasswordGenerator.generate(len);

        assertThat(pwd)
                .hasSize(len)
                .matches("[A-Za-z0-9]+");
    }

    @Test
    void zeroLengthReturnsEmptyString() {
        String pwd = PasswordGenerator.generate(0);
        assertThat(pwd).isEmpty();
    }

    @Test
    void subsequentGenerationsAreDifferentMostOfTheTime() {
        String a = PasswordGenerator.generate(16);
        String b = PasswordGenerator.generate(16);

        assertThat(a).isNotEqualTo(b);
    }
}
