package com.revplay.userservice.security;

public record AuthenticatedUserPrincipal(Long userId, String username, com.revplay.userservice.enums.UserRole role) {
}
