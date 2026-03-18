package com.revplay.playbackservice.controller;

import com.revplay.playbackservice.common.ApiResponse;
import com.revplay.playbackservice.dto.ListeningSessionDto;
import com.revplay.playbackservice.dto.StartSessionRequest;
import com.revplay.playbackservice.security.AuthContextUtil;
import com.revplay.playbackservice.service.ListeningSessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/playback/sessions")
public class ListeningSessionController {

    private final ListeningSessionService listeningSessionService;

    public ListeningSessionController(ListeningSessionService listeningSessionService) {
        this.listeningSessionService = listeningSessionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ListeningSessionDto>> startSession(
            @Valid @RequestBody StartSessionRequest request) {
        Long userId = AuthContextUtil.getCurrentUserId();
        return ResponseEntity
                .ok(ApiResponse.success(listeningSessionService.startSession(userId, request), "Session started"));
    }

    @PostMapping("/{sessionId}/end")
    public ResponseEntity<ApiResponse<Void>> endSession(@PathVariable Long sessionId) {
        Long userId = AuthContextUtil.getCurrentUserId();
        listeningSessionService.endSession(userId, sessionId);
        return ResponseEntity.ok(ApiResponse.success(null, "Session ended"));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<ListeningSessionDto>> getActiveSession() {
        Long userId = AuthContextUtil.getCurrentUserId();
        ListeningSessionDto session = listeningSessionService.getActiveSession(userId);
        return ResponseEntity
                .ok(ApiResponse.success(session, session != null ? "Active session retrieved" : "No active session"));
    }
}
