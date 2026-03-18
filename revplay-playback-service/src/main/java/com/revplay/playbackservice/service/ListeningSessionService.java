package com.revplay.playbackservice.service;

import com.revplay.playbackservice.dto.ListeningSessionDto;
import com.revplay.playbackservice.dto.StartSessionRequest;

public interface ListeningSessionService {
    ListeningSessionDto startSession(Long userId, StartSessionRequest request);

    void endSession(Long userId, Long sessionId);

    ListeningSessionDto getActiveSession(Long userId);
}
