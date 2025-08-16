package com.epam.crm.util;

import java.util.Set;


public class UniqueUsernameGenerator {

    public static String generate(Set<String> usernames, String name, String surname) {
        String base = name + "." + surname;
        String candidate = base;
        int i = 1;

        while (usernames.contains(candidate)) {
            candidate = base + i;
            i++;
        }

        return candidate;
    }

}
