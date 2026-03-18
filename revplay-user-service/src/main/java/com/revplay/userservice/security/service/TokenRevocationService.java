package com.revplay.userservice.security.service;

import java.time.Instant;

public interface TokenRevocationService {
    void revoke(String token, Instant expiry);

    void revokeAllForUser(Long userId);

    void registerIssuedToken(Long userId, String token, Instant expiry);

    boolean isRevoked(String token);
}
