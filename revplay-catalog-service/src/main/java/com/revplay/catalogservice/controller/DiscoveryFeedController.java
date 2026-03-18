package com.revplay.catalogservice.controller;

import com.revplay.catalogservice.common.response.ApiResponse;
import com.revplay.catalogservice.dto.response.DiscoverWeeklyResponse;
import com.revplay.catalogservice.dto.response.DiscoveryFeedResponse;
import com.revplay.catalogservice.service.DiscoveryFeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/discover")
@Tag(name = "Discovery Feed", description = "Personalized content discovery APIs")
public class DiscoveryFeedController {

    private final DiscoveryFeedService feedService;

    @GetMapping("/weekly/{userId}")
    @Operation(summary = "Get discover weekly recommendations for a user")
    public ResponseEntity<ApiResponse<DiscoverWeeklyResponse>> discoverWeekly(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Received discover weekly request userId={}, limit={}", userId, limit);
        DiscoverWeeklyResponse response = feedService.discoverWeekly(userId, limit);
        return ResponseEntity.ok(success(response, "Discover weekly fetched"));
    }

    @GetMapping("/feed/{userId}")
    @Operation(summary = "Get aggregated discovery feed for a user")
    public ResponseEntity<ApiResponse<DiscoveryFeedResponse>> homeFeed(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int sectionLimit) {
        log.info("Received discovery home feed request userId={}, sectionLimit={}", userId, sectionLimit);
        DiscoveryFeedResponse response = feedService.homeFeed(userId, sectionLimit);
        return ResponseEntity.ok(success(response, "Discovery feed fetched"));
    }

    @GetMapping("/feed")
    @Operation(summary = "Get personalized discovery feed")
    public ResponseEntity<ApiResponse<DiscoveryFeedResponse>> getDiscoveryFeed() {
        log.info("Received request to fetch anonymous discovery feed");
        DiscoveryFeedResponse response = feedService.getFeed();
        return ResponseEntity.ok(success(response, "Feed fetched"));
    }

    private <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
