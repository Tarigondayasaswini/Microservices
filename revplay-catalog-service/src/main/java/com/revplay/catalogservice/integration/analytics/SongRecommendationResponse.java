package com.revplay.catalogservice.integration.analytics;
public record SongRecommendationResponse(Long songId, String title, Long artistId, String artistName, Long score) {}
