package com.revplay.playbackservice.service;

import com.revplay.playbackservice.enums.UserRole;
import com.revplay.playbackservice.security.AuthenticatedUserPrincipal;
import io.jsonwebtoken.Claims;

import java.time.Instant;

public interface JwtService {

    String generateAccessToken(Long userId, String username, UserRole role);

    String generateRefreshToken(Long userId, String username, UserRole role);

    Claims parseToken(String token);

    boolean isAccessToken(String token);

    boolean isRefreshToken(String token);

    AuthenticatedUserPrincipal toPrincipal(String token);

    Instant getExpiry(String token);
}
