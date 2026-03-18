package com.revplay.playbackservice.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.Map;

public class AuthContextUtil {
    private static final Logger log = LoggerFactory.getLogger(AuthContextUtil.class);

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            log.warn("Playback-service: Authentication is null or not authenticated");
            return null;
        }
        Object principal = auth.getPrincipal();
        log.info("Playback-service: Current principal type: {}, value: {}", 
                principal != null ? principal.getClass().getName() : "null", principal);
        
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
