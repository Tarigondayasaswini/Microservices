package com.revplay.analyticsservice.service;

import com.revplay.analyticsservice.security.AuthenticatedUserPrincipal;
import io.jsonwebtoken.Claims;

import java.time.Instant;

public interface JwtService {

    Claims parseToken(String token);

    boolean isAccessToken(String token);

    boolean isRefreshToken(String token);

    AuthenticatedUserPrincipal toPrincipal(String token);

    Instant getExpiry(String token);
}


