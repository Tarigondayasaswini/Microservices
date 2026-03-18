package com.revplay.playbackservice.service.impl;

import com.revplay.playbackservice.client.AnalyticsServiceClient;
import com.revplay.playbackservice.client.CatalogServiceClient;
import com.revplay.playbackservice.dto.response.QueueItemResponse;
import com.revplay.playbackservice.dto.response.SongRecommendationResponse;
import com.revplay.playbackservice.dto.response.SongResponse;
import com.revplay.playbackservice.exception.PlaybackNotFoundException;
import com.revplay.playbackservice.exception.PlaybackValidationException;
import com.revplay.playbackservice.service.AutoplayService;
import com.revplay.playbackservice.service.QueueService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AutoplayServiceImpl implements AutoplayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoplayServiceImpl.class);

    private final QueueService queueService;
    private final AnalyticsServiceClient analyticsServiceClient;
    private final CatalogServiceClient catalogServiceClient;

    public AutoplayServiceImpl(
            QueueService queueService,
            AnalyticsServiceClient analyticsServiceClient,
            CatalogServiceClient catalogServiceClient
    ) {
        this.queueService = queueService;
        this.analyticsServiceClient = analyticsServiceClient;
        this.catalogServiceClient = catalogServiceClient;
    }

    @Override
    public SongResponse getNextSong(Long userId, Long currentSongId) {
        LOGGER.info("Resolving autoplay next song for userId={}, currentSongId={}", userId, currentSongId);
        if (userId == null || currentSongId == null) {
            throw new PlaybackValidationException("userId and currentSongId are required");
        }

        SongResponse nextFromQueue = tryQueueNextSong(userId, currentSongId);
        if (nextFromQueue != null) {
            return nextFromQueue;
        }

        return fallbackToRecommendations(currentSongId);
    }

    private SongResponse tryQueueNextSong(Long userId, Long currentSongId) {
        List<QueueItemResponse> queue = queueService.getQueue(userId);
        if (queue.isEmpty()) {
            LOGGER.info("Queue is empty for userId={}; falling back to recommendations", userId);
            return null;
        }

        QueueItemResponse currentQueueItem = queue.stream()
                .filter(item -> currentSongId.equals(item.songId()))
                .findFirst()
                .orElse(null);
        if (currentQueueItem == null) {
            LOGGER.info("Current songId={} not found in queue for userId={}; falling back to recommendations", currentSongId, userId);
            return null;
        }

        QueueItemResponse nextQueueItem = queueService.next(userId, currentQueueItem.queueId());
        if (nextQueueItem == null || nextQueueItem.songId() == null) {
            LOGGER.info("Next queue item is not a song; falling back to recommendations");
            return null;
        }
        return catalogServiceClient.getSongById(nextQueueItem.songId()).getBody().getData();
    }

    private SongResponse fallbackToRecommendations(Long currentSongId) {
        List<SongRecommendationResponse> recommendations = analyticsServiceClient.similarSongs(currentSongId, 1);
        if (recommendations == null || recommendations.isEmpty()) {
            LOGGER.warn("No recommendations found for currentSongId={}", currentSongId);
            throw new PlaybackNotFoundException("No autoplay song available");
        }

        Long recommendedSongId = recommendations.get(0).songId();
        LOGGER.info("Using recommended songId={} for autoplay fallback", recommendedSongId);
        return catalogServiceClient.getSongById(recommendedSongId).getBody().getData();
    }

}
