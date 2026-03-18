package com.revplay.analyticsservice.service;

import com.revplay.analyticsservice.dto.response.ForYouRecommendationsResponse;
import com.revplay.analyticsservice.dto.response.SongRecommendationResponse;

import java.util.List;

public interface RecommendationService {

    List<SongRecommendationResponse> similarSongs(Long songId, int limit);

    ForYouRecommendationsResponse forUser(Long userId, int limit);
}



