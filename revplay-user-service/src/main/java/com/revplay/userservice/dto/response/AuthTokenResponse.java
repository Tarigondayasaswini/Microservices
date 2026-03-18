package com.revplay.userservice.dto.response;

public record AuthTokenResponse(
        String tokenType,
        String accessToken,
        long expiresIn,
        String refreshToken,
        long refreshExpiresIn,
        UserResponse user) {
}
