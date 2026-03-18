package com.revplay.catalogservice.util;



import com.revplay.catalogservice.exception.UnauthorizedException;
import com.revplay.catalogservice.enums.UserRole;

import org.springframework.stereotype.Component;

@Component
public class AccessValidator {
    public void requireArtistOrAdmin(String role) {
        String normalizedRole = normalizeRole(role);
        if (!UserRole.ARTIST.name().equalsIgnoreCase(normalizedRole) && !UserRole.ADMIN.name().equalsIgnoreCase(normalizedRole)) {
            throw new UnauthorizedException("Access denied");
        }
    }

    public void requireAdmin(String role) {
        String normalizedRole = normalizeRole(role);
        if (!UserRole.ADMIN.name().equalsIgnoreCase(normalizedRole)) {
            throw new UnauthorizedException("Access denied");
        }
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "";
        }
        String normalized = role.trim().toUpperCase();
        if (normalized.startsWith("ROLE_")) {
            return normalized.substring(5);
        }
        return normalized;
    }
}
