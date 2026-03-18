package com.revplay.catalogservice.dto.response;

public record PopularPodcastItemResponse(
        Long podcastId,
        String title,
        Long playCount
) {
}


