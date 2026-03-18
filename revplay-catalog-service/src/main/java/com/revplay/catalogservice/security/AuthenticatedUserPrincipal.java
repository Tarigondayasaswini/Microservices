package com.revplay.catalogservice.security;

import com.revplay.catalogservice.enums.UserRole;

public record AuthenticatedUserPrincipal(Long userId, String username, UserRole role) {
}
