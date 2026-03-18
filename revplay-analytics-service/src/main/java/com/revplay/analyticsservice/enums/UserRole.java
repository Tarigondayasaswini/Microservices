package com.revplay.analyticsservice.enums;

public enum UserRole {
    ADMIN,
    ARTIST,
    LISTENER;

    public static UserRole from(String role) {
        if (role == null) return LISTENER;
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return LISTENER;
        }
    }
}
