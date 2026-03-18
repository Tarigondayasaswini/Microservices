package com.revplay.playlistservice.security;

import com.revplay.playlistservice.enums.UserRole;

public record AuthenticatedUserPrincipal(Long userId, String username, UserRole role) {
}
