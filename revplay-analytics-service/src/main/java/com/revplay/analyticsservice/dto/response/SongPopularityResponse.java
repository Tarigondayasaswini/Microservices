package com.revplay.analyticsservice.dto.response;

public record SongPopularityResponse(
        Long songId,
        String title,
        Long playCount,
        Long favoriteCount
) {
}





