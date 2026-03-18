package com.revplay.catalogservice.dto.response;

public record DiscoveryRecommendationItemResponse(
        Long songId,
        String title,
        Long artistId,
        String artistName,
        Long score
) {
}


