package com.revplay.playlistservice.service;

public interface PlaybackRateLimiterService {

    void ensureWithinLimit(String key, int maxRequests, int windowSeconds, String message);
}



