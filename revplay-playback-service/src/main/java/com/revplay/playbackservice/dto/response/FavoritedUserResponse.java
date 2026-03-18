package com.revplay.playbackservice.dto.response;

import java.time.Instant;

public record FavoritedUserResponse(
        Long userId,
        String username,
        String email,
        String fullName,
        Instant favoritedAt
) {
}





