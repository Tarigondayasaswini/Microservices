package com.revplay.playbackservice.enums;

import java.util.Arrays;

public enum UserRole {
    USER,
    ARTIST,
    ADMIN,
    LISTENER;

    public static UserRole from(String value) {
        if (value == null) return USER;
        return Arrays.stream(values())
                .filter(r -> r.name().equalsIgnoreCase(value) || ("ROLE_" + r.name()).equalsIgnoreCase(value))
                .findFirst()
                .orElse(USER);
    }
}
