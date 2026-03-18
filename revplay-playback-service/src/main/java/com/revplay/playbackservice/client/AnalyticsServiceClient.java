package com.revplay.playbackservice.client;

import com.revplay.playbackservice.dto.response.SongRecommendationResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "analytics-service", path = "/api/v1/recommendations")
public interface AnalyticsServiceClient {

    @GetMapping("/similar")
    List<SongRecommendationResponse> similarSongs(
            @RequestParam("songId") Long songId,
            @RequestParam(value = "limit", defaultValue = "10") int limit);
}
