package com.revplay.playbackservice.service.impl;

import com.revplay.playbackservice.dto.ListeningSessionDto;
import com.revplay.playbackservice.dto.StartSessionRequest;
import com.revplay.playbackservice.entity.ListeningSession;
import com.revplay.playbackservice.exception.ResourceNotFoundException;
import com.revplay.playbackservice.repository.ListeningSessionRepository;
import com.revplay.playbackservice.service.ListeningSessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class ListeningSessionServiceImpl implements ListeningSessionService {

    private final ListeningSessionRepository listeningSessionRepository;

    public ListeningSessionServiceImpl(ListeningSessionRepository listeningSessionRepository) {
        this.listeningSessionRepository = listeningSessionRepository;
    }

    @Transactional
    @Override
    public ListeningSessionDto startSession(Long userId, StartSessionRequest request) {
        // End any existing active session and start a new one
        Optional<ListeningSession> existingSession = listeningSessionRepository
                .findTopByUserIdOrderByStartedAtDesc(userId);

        if (existingSession.isPresent() && existingSession.get().getEndedAt() == null) {
            ListeningSession sessionToClose = existingSession.get();
            sessionToClose.setEndedAt(Instant.now());
            listeningSessionRepository.save(sessionToClose);
        }

        ListeningSession newSession = new ListeningSession();
        newSession.setUserId(userId);
        newSession.setDeviceInfo(request.getDeviceInfo());

        ListeningSession saved = listeningSessionRepository.save(newSession);
        return mapToDto(saved);
    }

    @Transactional
    @Override
    public void endSession(Long userId, Long sessionId) {
        ListeningSession session = listeningSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if (!session.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Session not found");
        }

        if (session.getEndedAt() == null) {
            session.setEndedAt(Instant.now());
            listeningSessionRepository.save(session);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ListeningSessionDto getActiveSession(Long userId) {
        Optional<ListeningSession> session = listeningSessionRepository.findTopByUserIdOrderByStartedAtDesc(userId);

        if (session.isEmpty() || session.get().getEndedAt() != null) {
            return null;
        }

        return mapToDto(session.get());
    }

    private ListeningSessionDto mapToDto(ListeningSession session) {
        return ListeningSessionDto.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .deviceInfo(session.getDeviceInfo())
                .build();
    }
}
