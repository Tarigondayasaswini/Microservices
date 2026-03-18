package com.revplay.userservice.enums;

public enum UserRole {
    LISTENER, ARTIST, ADMIN;

    public static UserRole from(String roleStr) {
        if (roleStr == null || roleStr.isBlank()) {
            return LISTENER;
        }
        try {
            return UserRole.valueOf(roleStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return LISTENER;
        }
    }
}
