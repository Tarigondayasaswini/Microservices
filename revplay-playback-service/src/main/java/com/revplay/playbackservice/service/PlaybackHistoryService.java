package com.revplay.playbackservice.service;

import com.revplay.playbackservice.dto.PlaybackHistoryDto;
import com.revplay.playbackservice.dto.RecordPlaybackRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlaybackHistoryService {
    void recordPlayback(Long userId, RecordPlaybackRequest request);

    Page<PlaybackHistoryDto> getUserHistory(Long userId, Pageable pageable);
}
