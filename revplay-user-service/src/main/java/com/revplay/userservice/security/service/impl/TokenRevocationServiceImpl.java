package com.revplay.userservice.security.service.impl;

import com.revplay.userservice.security.service.TokenRevocationService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenRevocationServiceImpl implements TokenRevocationService {

    // In-memory revocation for simplicity as requested (beginner friendly)
    // Production would use Redis
    private final Set<String> revokedTokens = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void revoke(String token, Instant expiry) {
        if (token != null) {
            revokedTokens.add(token);
        }
    }

    @Override
    public void revokeAllForUser(Long userId) {
        // Mock implementation. Real implementation would track tokens by user
    }

    @Override
    public void registerIssuedToken(Long userId, String token, Instant expiry) {
        // Mock implementation
    }

    @Override
    public boolean isRevoked(String token) {
        return revokedTokens.contains(token);
    }
}
