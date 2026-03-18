package com.revplay.catalogservice.security.service;

import com.revplay.catalogservice.security.AuthenticatedUserPrincipal;
import java.time.Instant;

public interface JwtService {
    String generateAccessToken(com.revplay.catalogservice.entity.User user);

    String generateRefreshToken(com.revplay.catalogservice.entity.User user);

    boolean isAccessToken(String token);

    boolean isRefreshToken(String token);

    AuthenticatedUserPrincipal toPrincipal(String token);

    Instant getExpiry(String token);
}
