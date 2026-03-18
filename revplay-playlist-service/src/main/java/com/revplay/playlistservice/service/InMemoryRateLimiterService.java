package com.revplay.playlistservice.service;

public interface InMemoryRateLimiterService {

    void ensureWithinLimit(String key, int maxRequests, int windowSeconds, String message);
}


