package com.revplay.playlistservice.service;

public interface ContentReferenceValidationService {
    void validateSongExists(Long songId);

    void validateLikeTargetExists(String type, Long targetId);
}
