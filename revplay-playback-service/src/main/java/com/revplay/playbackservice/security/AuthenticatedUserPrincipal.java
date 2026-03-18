package com.revplay.playbackservice.security;

import com.revplay.playbackservice.enums.UserRole;

public record AuthenticatedUserPrincipal(Long userId, String username, UserRole role) {
}
