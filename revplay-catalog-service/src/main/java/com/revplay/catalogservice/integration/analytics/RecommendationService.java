package com.revplay.catalogservice.integration.analytics;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class RecommendationService {
    public ForYouRecommendationsResponse forUser(Long userId, int limit) {
        return new ForYouRecommendationsResponse(List.of(), List.of());
    }
    public List<SongRecommendationResponse> getTrendingSongs() { return List.of(); }
}
