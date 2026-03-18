package com.revplay.catalogservice.security;

import com.revplay.catalogservice.enums.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
public class AuthContextUtil {

    public static Long requireCurrentUserId() {
        Long userId = getCurrentUserIdOrNull();
        if (userId == null) {
            throw new AccessDeniedException("Authentication required");
        }
        return userId;
    }

    public static Long getCurrentUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Authentication is null or not authenticated");
            return null;
        }

        Object principal = authentication.getPrincipal();
        log.info("Current principal type: {}", principal != null ? principal.getClass().getName() : "null");
        Long extractedFromPrincipal = extractUserId(principal);
        if (extractedFromPrincipal != null) {
            return extractedFromPrincipal;
        }

        Object details = authentication.getDetails();
        return extractUserId(details);
    }

    // Keep the one most controllers might use
    public static Long getCurrentUserId() {
        return getCurrentUserIdOrNull();
    }

    public static void requireAdmin() {
        if (hasRole(UserRole.ADMIN.name())) {
            return;
        }
        throw new AccessDeniedException("Admin access required");
    }

    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String normalized = normalizeRole(role);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities != null) {
            for (GrantedAuthority authority : authorities) {
                if (authority != null && normalizeRole(authority.getAuthority()).equals(normalized)) {
                    return true;
                }
            }
        }

        String principalRole = extractRole(authentication.getPrincipal());
        return principalRole != null && normalizeRole(principalRole).equals(normalized);
    }

    private static Long extractUserId(Object source) {
        if (source == null) {
            return null;
        }
        log.debug("Attempting to extract userId from source: {}", source);
        if (source instanceof Number number) {
            return number.longValue();
        }
        if (source instanceof String text) {
            try {
                return Long.valueOf(text);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        if (source instanceof Map<?, ?> map) {
            Object id = map.get("userId");
            if (id == null) {
                id = map.get("user_id");
            }
            if (id == null) {
                id = map.get("sub");
            }
            return extractUserId(id);
        }

        try {
            Method method = source.getClass().getMethod("getUserId");
            Object value = method.invoke(source);
            return extractUserId(value);
        } catch (ReflectiveOperationException ignored) {
            try {
                Method method = source.getClass().getMethod("userId");
                Object value = method.invoke(source);
                return extractUserId(value);
            } catch (ReflectiveOperationException ignoredAgain) {
                return null;
            }
        }
    }

    private static String extractRole(Object source) {
        if (source == null) {
            return null;
        }
        if (source instanceof Map<?, ?> map) {
            Object value = map.get("role");
            return value != null ? value.toString() : null;
        }
        try {
            Method method = source.getClass().getMethod("getRole");
            Object value = method.invoke(source);
            return value != null ? value.toString() : null;
        } catch (ReflectiveOperationException ignored) {
            try {
                Method method = source.getClass().getMethod("role");
                Object value = method.invoke(source);
                return value != null ? value.toString() : null;
            } catch (ReflectiveOperationException ignoredAgain) {
                return null;
            }
        }
    }

    private static String normalizeRole(String role) {
        String normalized = role == null ? "" : role.trim().toUpperCase(Locale.ROOT);
        if (normalized.startsWith("ROLE_")) {
            return normalized.substring(5);
        }
        return normalized;
    }
}
