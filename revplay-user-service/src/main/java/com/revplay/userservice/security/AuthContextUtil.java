package com.revplay.userservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.Map;

public class AuthContextUtil {
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof AuthenticatedUserPrincipal p) {
            return p.userId();
        }
        return extractUserId(principal);
    }

    private static Long extractUserId(Object source) {
        if (source == null) return null;
        if (source instanceof Number number) return number.longValue();
        if (source instanceof String text) {
            try { return Long.valueOf(text); } catch (NumberFormatException ignored) { return null; }
        }
        if (source instanceof Map<?, ?> map) {
            Object id = map.get("userId");
            if (id == null) id = map.get("user_id");
            if (id == null) id = map.get("sub");
            if (id == null) id = map.get("id");
            return extractUserId(id);
        }
        try {
            Method method = source.getClass().getMethod("userId");
            return extractUserId(method.invoke(source));
        } catch (ReflectiveOperationException ignored) {
            try {
                Method method = source.getClass().getMethod("getUserId");
                return extractUserId(method.invoke(source));
            } catch (ReflectiveOperationException ignoredAgain) {
                return null;
            }
        }
    }
}
