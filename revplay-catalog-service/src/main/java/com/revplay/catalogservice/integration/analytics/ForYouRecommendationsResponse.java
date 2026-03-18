package com.revplay.catalogservice.integration.analytics;
import java.util.List;
public record ForYouRecommendationsResponse(List<SongRecommendationResponse> youMightLike, List<SongRecommendationResponse> popularWithSimilarUsers) {}
