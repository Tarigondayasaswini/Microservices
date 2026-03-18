package com.revplay.userservice.security.service;

import com.revplay.userservice.security.AuthenticatedUserPrincipal;
import java.time.Instant;

public interface JwtService {
    String generateAccessToken(com.revplay.userservice.entity.User user);

    String generateRefreshToken(com.revplay.userservice.entity.User user);

    boolean isAccessToken(String token);

    boolean isRefreshToken(String token);

    AuthenticatedUserPrincipal toPrincipal(String token);

    Instant getExpiry(String token);
}
