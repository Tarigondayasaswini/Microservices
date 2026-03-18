package com.revplay.playbackservice.dto.response;

public record SongRecommendationResponse(
    Long songId,
    String title,
    Long artistId,
    String artistName,
    Long score
) {}
