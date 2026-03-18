package com.revplay.playbackservice.controller;

import com.revplay.playbackservice.common.ApiResponse;
import com.revplay.playbackservice.dto.PlaybackHistoryDto;
import com.revplay.playbackservice.dto.RecordPlaybackRequest;
import com.revplay.playbackservice.security.AuthContextUtil;
import com.revplay.playbackservice.service.PlaybackHistoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/playback/history")
public class PlaybackHistoryController {

    private final PlaybackHistoryService playbackHistoryService;

    public PlaybackHistoryController(PlaybackHistoryService playbackHistoryService) {
        this.playbackHistoryService = playbackHistoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> recordPlayback(@Valid @RequestBody RecordPlaybackRequest request) {
        Long userId = AuthContextUtil.getCurrentUserId();
        playbackHistoryService.recordPlayback(userId, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Playback recorded successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PlaybackHistoryDto>>> getPlaybackHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Long userId = AuthContextUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                playbackHistoryService.getUserHistory(userId, PageRequest.of(page, size)),
                "Playback history retrieved"));
    }
}
