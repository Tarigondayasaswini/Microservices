package com.revplay.analyticsservice.dto.response;

public record ArtistDashboardResponse(
        Long artistId,
        Long totalSongs,
        Long totalPlays,
        Long totalFavorites
) {
}





