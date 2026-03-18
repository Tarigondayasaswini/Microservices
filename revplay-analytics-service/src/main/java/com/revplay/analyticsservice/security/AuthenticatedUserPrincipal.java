package com.revplay.analyticsservice.security;

import com.revplay.analyticsservice.enums.UserRole;

public record AuthenticatedUserPrincipal(Long userId, String username, UserRole role) {
}
