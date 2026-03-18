package com.revplay.catalogservice.security.service;
public interface PlaybackRateLimiterService {
    void ensureCanPlay(String userId, String entityType);
}
