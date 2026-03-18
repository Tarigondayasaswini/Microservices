package com.revplay.userservice.dto.response;

public record UserProfileResponse(
        Long userId,
        String fullName,
        String bio,
        String profilePictureUrl,
        String country
) {
}
