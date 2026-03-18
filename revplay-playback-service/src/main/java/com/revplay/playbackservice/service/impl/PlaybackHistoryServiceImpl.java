package com.revplay.playbackservice.service.impl;

import com.revplay.playbackservice.client.CatalogServiceClient;
import com.revplay.playbackservice.dto.PlaybackHistoryDto;
import com.revplay.playbackservice.dto.RecordPlaybackRequest;
import com.revplay.playbackservice.entity.PlaybackHistory;
import com.revplay.playbackservice.exception.InvalidPlaybackException;
import com.revplay.playbackservice.repository.PlaybackHistoryRepository;
import com.revplay.playbackservice.service.PlaybackHistoryService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaybackHistoryServiceImpl implements PlaybackHistoryService {

    private static final Logger log = LoggerFactory.getLogger(PlaybackHistoryServiceImpl.class);
    private final PlaybackHistoryRepository playbackHistoryRepository;
    private final CatalogServiceClient catalogServiceClient;

    public PlaybackHistoryServiceImpl(PlaybackHistoryRepository playbackHistoryRepository,
            CatalogServiceClient catalogServiceClient) {
        this.playbackHistoryRepository = playbackHistoryRepository;
        this.catalogServiceClient = catalogServiceClient;
    }

    @Transactional
    @Override
    public void recordPlayback(Long userId, RecordPlaybackRequest request) {
        // Validate song exists via feign client
        try {
            catalogServiceClient.getSongById(request.getSongId());
        } catch (FeignException.NotFound e) {
            throw new InvalidPlaybackException("Song with ID " + request.getSongId() + " does not exist.");
        } catch (FeignException e) {
            log.error("Failed to fetch song info from catalog service", e);
            throw new RuntimeException("Failed to validate song", e);
        }

        PlaybackHistory history = new PlaybackHistory();
        history.setUserId(userId);
        history.setSongId(request.getSongId());
        history.setDurationListenedSeconds(request.getDurationListenedSeconds());

        playbackHistoryRepository.save(history);

        // At industry scale, this would emit a Kafka event for Analytics and
        // Recommendations
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PlaybackHistoryDto> getUserHistory(Long userId, Pageable pageable) {
        return playbackHistoryRepository.findByUserIdOrderByPlayedAtDesc(userId, pageable)
                .map(this::mapToDto);
    }

    private PlaybackHistoryDto mapToDto(PlaybackHistory history) {
        return PlaybackHistoryDto.builder()
                .id(history.getId())
                .userId(history.getUserId())
                .songId(history.getSongId())
                .playedAt(history.getPlayedAt())
                .durationListenedSeconds(history.getDurationListenedSeconds())
                .build();
    }
}
